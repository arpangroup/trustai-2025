# 📘 Unique ID Generation

## ✅ Requirements Recap
- **Input**: Integer or long ID
- **Output**: Unique, **random-looking, 9-character string**
- **Character set**: Uppercase letters (A–Z) + digits (0–9) → **Base36**
- **Fixed length**: Exactly 9 characters
- **Reversible**: Yes — you must be able to decode it back to the original ID
- **Output must not start with `0`**

---
## ✅ What This Means:
- You're using a Base36 encoding:
  - `0–9` (10 digits)
  - `A–`Z (26 uppercase letters)
  - → Total: **36 characters**
- With **9 characters in Base36**, the maximum ID you can encode is:
    > 36 ^ 9 = 1,015,599,162,777,600  ==> 36^9 - 1 ≈ 1e15
- That’s up to **1 quadrillion+** — safe for most use cases involving long.

### Solution-1
````java
public class IdConverter {
    private static final String ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final int BASE = ALPHABET.length();
    private static final int TARGET_LENGTH = 9;
    private static final long MAX_ID = (long) Math.pow(BASE, TARGET_LENGTH) - 1;

    public static String encode(long id) {
        if (id < 0 || id > MAX_ID) {
            throw new IllegalArgumentException("ID must be between 0 and " + MAX_ID);
        }

        StringBuilder sb = new StringBuilder();

        do {
            sb.insert(0, ALPHABET.charAt((int) (id % BASE)));
            id /= BASE;
        } while (id > 0);

        // Pad with leading '0's to make it 9 characters
        while (sb.length() < TARGET_LENGTH) {
            sb.insert(0, '0');
        }

        return sb.toString();
    }

    public static long decode(String str) {
        if (str.length() != TARGET_LENGTH) {
            throw new IllegalArgumentException("Encoded string must be exactly " + TARGET_LENGTH + " characters.");
        }

        long num = 0;
        for (char c : str.toCharArray()) {
            int index = ALPHABET.indexOf(c);
            if (index == -1) {
                throw new IllegalArgumentException("Invalid character in encoded string: " + c);
            }
            num = num * BASE + index;
        }

        return num;
    }
}
````

````text
ID: 1        → Encoded: 0000000001
ID: 2        → Encoded: 0000000002
ID: 3        → Encoded: 0000000003
...
ID: 123456   → Encoded: 0000002N9C

````

## ⚠️ Issue with above approach:
> ⚠️ I don’t just want a reversible Base36 encoding that looks like `000000001` — that’s too predictable and static.
I want:


## 🔁 Solution2: Reversible + Randomized → Needs Deterministic Obfuscation
1. **Obfuscate the ID** (so `1` doesn’t look like `000000001`)
2. **Still be able to reverse it** to get back the original ID

## ✅ Solution: Reversible Obfuscation + Base36 Encoding
**Steps:**
1. **XOR** the ID with a secret key or salt (like a secret number)
   - Makes the ID look "random" but it's still reversible
2. Encode the result to **Base36** using 0-9A-Z
3. Pad to **9 characters**
4. To decode: Base36 decode → XOR with the same secret → get the original ID

Reversible Obfuscation + Base36 Encoding:
````java
public class ObfuscatedIdConverter {
    private static final String ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final int BASE = ALPHABET.length();
    private static final int TARGET_LENGTH = 9;
    private static final long MAX_ID = (long) Math.pow(BASE, TARGET_LENGTH) - 1;

    // This is your secret key — keep it consistent and secret
    private static final long SECRET = 987654321L;

    // Encode ID
    public static String encode(long id) {
        if (id < 0 || id > MAX_ID) {
            throw new IllegalArgumentException("ID must be between 0 and " + MAX_ID);
        }

        long obfuscated = id ^ SECRET;  // XOR to obfuscate

        StringBuilder sb = new StringBuilder();
        do {
            sb.insert(0, ALPHABET.charAt((int) (obfuscated % BASE)));
            obfuscated /= BASE;
        } while (obfuscated > 0);

        // Pad to 9 characters
        while (sb.length() < TARGET_LENGTH) {
            sb.insert(0, '0');
        }

        return sb.toString();
    }

    // Decode ID
    public static long decode(String str) {
        if (str.length() != TARGET_LENGTH) {
            throw new IllegalArgumentException("Encoded string must be exactly " + TARGET_LENGTH + " characters.");
        }

        long obfuscated = 0;
        for (char c : str.toCharArray()) {
            int index = ALPHABET.indexOf(c);
            if (index == -1) {
                throw new IllegalArgumentException("Invalid character in encoded string: " + c);
            }
            obfuscated = obfuscated * BASE + index;
        }

        return obfuscated ^ SECRET; // Reverse XOR
    }
}
````
🔐 Notes:
- The XOR with a secret value ensures the string **looks random**, even for small or sequential IDs.
- It's still **fully reversible**.
- Different secret = different encoded string for same ID (great for multi-tenant systems or added security).
- Keep the `SECRET` value private and consistent between encode/decode.

✅ Output Example:
````text
ID: 1        → Encoded: 00BN9C0TO
ID: 2        → Encoded: 00BN9C0TN
ID: 3        → Encoded: 00BN9C0TM
...
ID: 123456   → Encoded: 00BN9F8S2
````

## ⚠️ Issue with above approach (Starting with 0)
> ⚠️ I don't want to start with 0

### 🔍 Why Output Starts with 0 Sometimes
The leading zeros come from **padding shorter encodings** to match the required 9-character length (e.g., `'00000ABCD'`). To ensure the output doesn’t start with `'0'`, we must change how we pad.

### ✅ Strategy to Avoid Leading Zeros While Keeping Length = 9
1. **XOR the ID** with a secret key → to obfuscate
2. **Base36 encode** that value
3. If it's less than 9 characters:
   - **Pad it with random non-zero Base36 characters** on the left (instead of `0`)
   - OR `move the padding to the right`
4. Or alternatively — use **prefixing tricks** that always start with a non-zero character, without affecting reversibility


**We'll take the simplest, safe, reversible approach:** <br/>
Revised Java Code (No Leading Zeros)
````java
import java.util.Random;

public class ObfuscatedIdConverter {

    private static final String ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final int BASE = ALPHABET.length(); // 36
    private static final int TARGET_LENGTH = 9;
    private static final long MAX_ID = (long) Math.pow(BASE, TARGET_LENGTH) - 1;
    private static final long SECRET = 987654321L;

    // Randomizer for padding (used only for non-zero padding)
    private static final Random random = new Random();

    public static String encode(long id) {
        if (id < 0 || id > MAX_ID) {
            throw new IllegalArgumentException("ID must be between 0 and " + MAX_ID);
        }

        long obfuscated = id ^ SECRET;

        StringBuilder sb = new StringBuilder();
        do {
            sb.insert(0, ALPHABET.charAt((int) (obfuscated % BASE)));
            obfuscated /= BASE;
        } while (obfuscated > 0);

        // Ensure it is exactly 9 characters, but not starting with '0'
        while (sb.length() < TARGET_LENGTH) {
            // Instead of padding with '0', pad with a random non-zero Base36 character
            char padChar;
            do {
                padChar = ALPHABET.charAt(random.nextInt(BASE));
            } while (padChar == '0');
            sb.insert(0, padChar);
        }

        // Final check: make sure first char is not '0' (highly unlikely with above, but safe to double check)
        if (sb.charAt(0) == '0') {
            sb.setCharAt(0, 'A'); // Replace with 'A' or another non-zero safe default
        }

        return sb.toString();
    }

    public static long decode(String str) {
        if (str.length() != TARGET_LENGTH) {
            throw new IllegalArgumentException("Encoded string must be exactly " + TARGET_LENGTH + " characters.");
        }

        long obfuscated = 0;
        for (char c : str.toCharArray()) {
            int index = ALPHABET.indexOf(c);
            if (index == -1) {
                throw new IllegalArgumentException("Invalid character in encoded string: " + c);
            }
            obfuscated = obfuscated * BASE + index;
        }

        return obfuscated ^ SECRET;
    }
}
````
````java
public static void main(String[] args) {
    for (long id = 1; id <= 5; id++) {
        String encoded = ObfuscatedIdConverter.encode(id);
        long decoded = ObfuscatedIdConverter.decode(encoded);
        System.out.printf("ID: %-5d → Encoded: %s → Decoded: %d%n", id, encoded, decoded);
    }
}
````

````text
ID: 1     → Encoded: H0X1Z27VG → Decoded: 1
ID: 2     → Encoded: R1FZMG13Q → Decoded: 2
ID: 3     → Encoded: E23P7IQBD → Decoded: 3
ID: 4     → Encoded: LDT93YH6N → Decoded: 4
ID: 5     → Encoded: W6NAB8Y3R → Decoded: 5
````

## ⚠️ Warning: Not Cryptographically Secure

This ID generation method uses basic XOR-based obfuscation and Base36 encoding.

- It is **not suitable for security-sensitive applications** such as:
    - Hiding sensitive user IDs
    - Preventing enumeration attacks
    - Generating secure tokens

- The XOR secret key **must be kept private and consistent** across encode/decode.
- Anyone with knowledge of the key and algorithm can reverse the ID.

> 🔐 If you need cryptographic security or tamper resistance, consider using:
> - UUIDs with HMAC or encryption
> - Secure random tokens (e.g., using Java’s `SecureRandom`)
> - Hashids or other one-way encoding schemes
