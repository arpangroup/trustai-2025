# Internal API user handling


## Duplicating the internal-vs-user check in every controller method
Here are some common and clean approaches:

## 1. Base controller with helper method (simple but requires inheritance)
Create an abstract base controller class with a helper method that returns the "effective user" based on:
- If internal call: read acting user ID from header
- If external user call: read authenticated username from Authentication
````java
public abstract class BaseController {

    protected String getEffectiveUserId(HttpServletRequest request, Authentication authentication) {
        boolean isInternal = authentication.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_INTERNAL"));

        if (isInternal) {
            // Internal call - get acting user ID from header
            String actingUserId = request.getHeader("X-Acting-User-Id");
            if (actingUserId == null || actingUserId.isBlank()) {
                throw new IllegalArgumentException("Missing acting user ID for internal call");
            }
            return actingUserId;
        } else {
            // External user - use authenticated user
            return authentication.getName();
        }
    }
}
````
Then your controllers extend this base class:
````java
@RestController
@RequestMapping("/users")
public class UserController extends BaseController {

    @GetMapping("/me")
    public ResponseEntity<UserInfo> getMyInfo(HttpServletRequest request, Authentication authentication) {
        String userId = getEffectiveUserId(request, authentication);
        UserInfo info = userService.findById(userId);
        return ResponseEntity.ok(info);
    }
}
````

## 2. HandlerMethodArgumentResolver (best for injection, no inheritance)
Create a custom annotation, e.g., `@EffectiveUserId`, and then implement a `HandlerMethodArgumentResolver` that injects the effective user ID automatically into your controller method parameter.

**Step 1️⃣ Create the annotation**
````java
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface EffectiveUserId {}
````

**Step 2️⃣ Create the argument resolver**
````java
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@RequiredArgsConstructor
public class EffectiveUserIdArgumentResolver implements HandlerMethodArgumentResolver {
    
    private final UserService userService; // used to resolve username -> userId

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(EffectiveUserId.class)
               && parameter.getParameterType().equals(String.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        boolean isInternal = auth.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_INTERNAL"));

        if (isInternal) {
            // Internal service call
            String actingUserIdHeader = request.getHeader("X-Acting-User-Id");
            if (actingUserIdHeader == null || actingUserIdHeader.isBlank()) {
                throw new IllegalArgumentException("Missing acting user ID header for internal call");
            }
            //return actingUserIdHeader;
            return Long.parseLong(actingUserIdHeader);
        } else {
            // External user call
            String username = authentication.getName();
            
            // Return the authenticated user's username (or ID if you prefer)
            //return username;

            return userService.findByUsername(username)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"))
                    .getId();
        }
    }
}
````

**Step 3️⃣: Register argument resolver in your MVC config**
````java
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new EffectiveUserIdArgumentResolver());
    }
}
````

**Step 4️⃣: Use in controller method**
````java
@GetMapping("/me")
public ResponseEntity<UserInfo> getMyInfo(@EffectiveUserId String effectiveUserId) {
    UserInfo info = userService.findById(effectiveUserId);
    return ResponseEntity.ok(info);
}

// Works for both internal and external calls
@GetMapping("/{userId}")
public ResponseEntity<UserInfo> getUser(
        @PathVariable String userId,
        @EffectiveUserId String effectiveUserId) {

    // Optional: restrict external users to their own info
    if (!userId.equals(effectiveUserId)) {
        // Only internal calls can fetch other users
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
    }

    UserInfo info = userService.findById(userId);
    return ResponseEntity.ok(info);
}

// Shortcut endpoint for the current user
@GetMapping("/me")
public ResponseEntity<UserInfo> getCurrentUser(@EffectiveUserId String effectiveUserId) {
    UserInfo info = userService.findById(effectiveUserId);
    return ResponseEntity.ok(info);
}
````


## 3. Filter or Interceptor that sets "effective user" in a ThreadLocal or Request attribute
You can create a servlet filter or Spring interceptor that on every request:
- Checks if internal token or normal user token
- Determines the effective user ID
- Stores it in a `ThreadLocal` or request attribute

---

## Recommendation
- For clean controller code and flexibility, option 2 (**HandlerMethodArgumentResolver**) is best
- It decouples your auth logic from controllers
- Lets you inject the effective user ID anywhere as a method parameter without inheritance








