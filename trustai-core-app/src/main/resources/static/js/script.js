
let allUsers = [];
let selectedUserId = null;
let selectedUserRank = null;
function loadQueries() {
  const container = document.getElementById("query-container");
  container.innerHTML = "";

  queryData.forEach(item => {
    const block = document.createElement("div");

    block.innerHTML = `
            <h4>${item.title}</h4>
            <div class="copy-section" data-title="${item.title.replace(/"/g, '&quot;')}">
              <pre class="copy-text">${item.query}</pre>
              <img src="clipboard.png" alt="Copy" class="copy-icon" onclick="copyToClipboard(this)" title="Copy to Clipboard" />
              <button class="exec-btn" onclick="executeFromBlock(this)">▶️ Execute</button>
              <div class="copy-status" style="color: green; margin-top: 10px; display: none;">Copied text</div>
            </div>
          `;

    container.appendChild(block);
  });
}

// Load queries on page load
window.addEventListener("DOMContentLoaded", loadQueries);


function buildTree(node, level = 0) {
  let html = `<li><div class="node"
    data-level="${level}"
    title="User ID: ${node.userId}"
    onclick="showUserInfo('${node.userId}', '${node.username}', '${node.walletBalance}', '${node.userRank}', event)">
    ${node.username}
    </div>`;
  if (node.children && node.children.length > 0) {
    html += `<ul>`;
    node.children.forEach(child => {
      html += buildTree(child, level + 1); // Pass incremented level
    });
    html += `</ul>`;
  }
  html += `</li>`;
  return html;
}

async function loadTree() {
  try {
    const urlParams = new URLSearchParams(window.location.search);
    const userId = urlParams.get('userId') || 1;
    const maxLevel = urlParams.get('level') || 3;
    const response = await fetch(`/api/v1/tree/${userId}?maxLevel=${maxLevel}`);
    const treeData = await response.json();
    console.log("TREE_DATA: ", treeData);
    document.getElementById('mlmTree').innerHTML = `<ul>${buildTree(treeData)}</ul>`;
  } catch (error) {
    console.error('Failed to load tree data:', error);
    document.getElementById('mlmTree').innerText = 'Failed to load tree.';
  }
}

loadTree();

function loadUsers() {
  const referralSelect = document.getElementById('referralSelect');
  const userSelect = document.getElementById('userSelect');

  // Reset dropdowns
  referralSelect.innerHTML = '<option value="">-- Select Referrer --</option>';
  userSelect.innerHTML = '<option value="">-- Select User --</option>';

  // Simulated user fetch (replace with actual fetch from backend if needed)
  /*const users = [
    { username: "alice", referral_code: "R001" },
    { username: "bob", referral_code: "R002" },
    { username: "carol", referral_code: "R003" }
  ];*/


  fetch("/api/v1/users")
    .then(response => response.json())
    .then(data => {
      console.log("USERS: ", data);
      const users = data;
      allUsers = users;

      users.forEach(user => {
        // Add to referralSelect: value = referralCode, text = username
        const referralOption = document.createElement("option");
        referralOption.text = user.username;
        referralOption.value = user.referralCode;
        referralSelect.add(referralOption);


        // Add to userSelect: value = id, text = username
        const userOption = document.createElement("option");
        userOption.value = user.id;
        userOption.text = user.username;
        if (user.id == 1) { // if root user, not able to deposit to his own account
          userOption.disabled = true;
        }
        if (user.id == selectedUserId) {
          userOption.selected = true;
        }
        userSelect.add(userOption);
      });
    })
    .catch(error => console.error('Error:', error));
}


function loadTransaction() {
  if (!selectedUserId) return alert("Please enter a user ID.");
  console.log("loadTransaction for selectedUserId: ", selectedUserId);
  const baseUrl = '/api/v1/transactions';
  const url = (selectedUserId && selectedUserId !== '1') ? `${baseUrl}/${selectedUserId}` : baseUrl;

  fetch(url)
    .then(response => response.json())
    .then(data => {
      console.log("TRANSACTIONS: ", data);
      const tbody = document.querySelector('#transactionsTable tbody');
      tbody.innerHTML = ''; // Clear previous rows

      data.content.forEach(txn => {
        const tr = document.createElement('tr');

        tr.innerHTML = `
                    <td>${txn.id}</td>
                    <td style="color: ${txn.amount >= 0 ? 'green' : 'red'};">${txn.amount.toFixed(2)}</td>
                    <td>${txn.balance.toFixed(2)}</td>
                    <td>${txn.remarks}</td>
                  `;

        tbody.appendChild(tr);
      });
    })
    .catch(error => console.error('Error:', error));
}




// Zoom
let scale = 1;
const treeWrapper = document.getElementById('treeWrapper');

function zoomIn() {
  scale += 0.1;
  treeWrapper.style.transform = `scale(${scale})`;
}

function zoomOut() {
  scale = Math.max(0.2, scale - 0.1);
  treeWrapper.style.transform = `scale(${scale})`;
}

function resetZoom() {
  scale = 1;
  treeWrapper.style.transform = `scale(1)`;
}

function addUsers() {
  fetch("/api/v1/register/add-users", {
    method: 'POST',
    headers: { "Content-Type": "application/json" },
    })
    .then(response => response.json())
    .then(data => {
      console.log("reloading.....");
      //location.reload();
      loadTree();
    })
    .catch(error => console.error('Error:', error));
}

/*function showUserInfo(userId, username, walletBalance, event) {
  const label = document.getElementById('userLabel');
  label.textContent = `Selected User: ${username} (ID: ${userId})`;

   // Remove 'selected' class from any previously selected node
  document.querySelectorAll('.node.selected').forEach(node => {
    node.classList.remove('selected');
  });

  // Highlight the clicked node
  const clickedNode = event.currentTarget;
  clickedNode.classList.add('selected');
}*/


let selectedNodeElement = null;
function showUserInfo(userId, username, walletBalance, userRank, event) {
  selectedUserId = userId;
  clearHighlights(true);
  console.log("SELECTED_USER_ID: ", selectedUserId);

  // Remove previous selection
  if (selectedNodeElement) {
    selectedNodeElement.classList.remove("selected");
  }

  // Mark current node as selected
  selectedNodeElement = event.target;
  selectedNodeElement.classList.add("selected");

  // Show label (you can customize where to show)
  const label = document.getElementById('selectedUserLabel').textContent = `Selected User: ${username} (ID: ${userId}) ℹ️ Balance: ${walletBalance} ℹ️ Rank: ${userRank}`;
  //getUserRank(userId);

  // 🔄 Update or append `id` query param in the URL without reloading
  const url = new URL(window.location);
  url.searchParams.set("userId", userId); // adds or updates the `id` param
  window.history.replaceState({}, "", url); // updates the URL in the browser without reload
}

async function getUserRank(userId) {
    const response = await fetch(`/api/v1/hierarchy/${userId}/statistics`);
    //const rank  = (await response.text()).replace(/^"|"$/g, ''); // Assuming it's text/string not JSON
    const data  = await response.json();
    console.log("HIERARCHY_STATISTICS: ", data);
    selectedUserRank = data.rank;

    const label = document.getElementById('selectedUserLabel');
    label.textContent += ` ℹ️ Rank: ${data.rank}`;

    const downlines = data.downlines;
    let htmlContent = "";
    for (const [level, users] of Object.entries(downlines)) {
        const count = users.length;
        htmlContent += `<div><strong>Level ${level} (${count} users):</strong> ${users.join(", ")}</div>`;
    }
    document.getElementById('donlineUserLevel').innerHTML = htmlContent;

}


// Sidebar
/*function toggleSidebar(type) {
  const sidebarSql = document.getElementById('sidebar-sql');
  const sidebarInfo = document.getElementById('sidebar-info');
  const sidebarRegister = document.getElementById('sidebar-register');
  const backdrop = document.getElementById('backdrop');

  if (type === 'sql') {
    sidebarSql.classList.toggle('open');
  } else if (type === 'info') {
    sidebarInfo.classList.toggle('open');
  } else if (type === 'register') {
    loadUsers();
    sidebarRegister.classList.toggle('open');
  }

  // Show backdrop if either sidebar is open
  if (sidebarSql.classList.contains('open') || sidebarInfo.classList.contains('open') || sidebarInfo.classList.contains('open') || sidebarRegister.classList.contains('open')) {
    backdrop.classList.add('active');
  } else {
    backdrop.classList.remove('active');
  }
}*/

function toggleSidebar(id) {
  // Close all sidebars by removing 'open' class
  document.querySelectorAll('.sidebar').forEach(s => s.classList.remove('open'));

  // Show the target sidebar
  const target = document.getElementById(id);
  if (target) {
    target.classList.add('open');

    // Show backdrop
    document.getElementById('backdrop').classList.add('active');

    // Load referral users if opening register sidebar
    if (id === 'sidebar-register' || id === 'sidebar-deposit') {
      loadUsers();
    }

    // Load transactions if opening transactions sidebar
    if (id === 'sidebar-transactions') {
      loadTransaction();
    }
    if (id === 'sidebar-config') {
    }
    if (id === 'sidebar-products') {
      loadProducts();
      loadRanks();
    }
    if(id === 'sidebar-income') {
        loadIncome();
    }
  }
}

function closeAllSidebars() {
  /*document.getElementById('sidebar-sql').classList.remove('open');
  document.getElementById('sidebar-info').classList.remove('open');
  document.getElementById('sidebar-register').classList.remove('open');
  document.getElementById('sidebar-deposit').classList.remove('open');
  document.getElementById('sidebar-transactions').classList.remove('open');
  document.getElementById('sidebar-config').classList.remove('open');
  document.getElementById('sidebar-teams').classList.remove('open');
  document.getElementById('sidebar-incomes').classList.remove('open');
  document.getElementById('sidebar-products').classList.remove('open');
  document.getElementById('backdrop').classList.remove('active');*/

  document.querySelectorAll('[id^="sidebar-"]').forEach(el => {
    el.classList.remove('open');
  });
  document.getElementById('backdrop')?.classList.remove('active');
}

function copyToClipboard(icon) {
  // Find the related copy-section container
  const copySection = icon.closest('.copy-section');
  const textElem = copySection.querySelector('.copy-text');
  const statusElem = copySection.querySelector('.copy-status');

  const text = textElem.innerText;

  navigator.clipboard.writeText(text).then(() => {
    statusElem.textContent = 'Copied text';
    statusElem.style.color = 'green';
    statusElem.style.display = 'block';

    setTimeout(() => {
      statusElem.style.display = 'none';
    }, 2000);
  }).catch(() => {
    statusElem.textContent = 'Failed to copy';
    statusElem.style.color = 'red';
    statusElem.style.display = 'block';

    setTimeout(() => {
      statusElem.style.display = 'none';
    }, 2000);
  });
}

// Execute button (demo logic)
async function runQuery() {
  const query = document.getElementById('sqlInput').value;
  const resultDiv = document.getElementById("result");
  const errorDiv = document.getElementById("error");
  resultDiv.innerHTML = "";
  errorDiv.textContent = "";

  try {
    const response = await fetch("/api/sql/execute", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ query }),
    });

    if (!response.ok) {
      const errorData = await response.json();
      throw new Error(errorData.message || "Query execution failed");
    }

    const data = await response.json();

    if (!Array.isArray(data) || data.length === 0) {
      resultDiv.innerHTML = "<p>No results found.</p>";
      return;
    }

    const table = document.createElement("table");
    const thead = document.createElement("thead");
    const headerRow = document.createElement("tr");

    Object.keys(data[0]).forEach((key) => {
      const th = document.createElement("th");
      th.textContent = key;
      headerRow.appendChild(th);
    });

    thead.appendChild(headerRow);
    table.appendChild(thead);

    const tbody = document.createElement("tbody");

    data.forEach((row) => {
      const tr = document.createElement("tr");
      Object.values(row).forEach((value) => {
        const td = document.createElement("td");
        td.textContent = value !== null ? value : "";
        tr.appendChild(td);
      });
      tbody.appendChild(tr);
    });

    table.appendChild(tbody);
    resultDiv.appendChild(table);
  } catch (err) {
    errorDiv.textContent = err.message;
  }
}
const sqlInput = document.getElementById("sqlInput");
sqlInput.addEventListener("keydown", function (e) {
  if (e.ctrlKey && e.key === "Enter") {
    e.preventDefault();
    runQuery();
  }
});

function highlightLevel(relativeLevel) {
  if (!selectedNodeElement) {
    alert("Please select a user first.");
    return;
  }

  clearHighlights(true);

  const baseLevel = parseInt(selectedNodeElement.dataset.level);
  const targetLevel = baseLevel + relativeLevel;

  // Select LI node (tree structure)
  const selectedLI = selectedNodeElement.closest("li");

  // Search inside selected subtree only
  const descendants = selectedLI.querySelectorAll(".node");

  // Highlight target nodes
  descendants.forEach(node => {
    const nodeLevel = parseInt(node.dataset.level);
    if (nodeLevel === targetLevel) {
      node.classList.add("level-highlight");
    }
  });

  // Highlight the clicked button
  document.querySelectorAll(".controls-highlight button").forEach(btn => {
    btn.classList.remove("selected");

    if (btn.dataset.level === String(relativeLevel)) {
      btn.classList.add("selected");
    }
  });
}

function clearHighlights(removeButtonHighlight = true) {
  // Remove node highlights
  document.querySelectorAll('.node.level-highlight').forEach(el => {
    el.classList.remove('level-highlight');
  });

  // Optionally remove highlight button styles
  if (removeButtonHighlight) {
    document.querySelectorAll('.controls-highlight button').forEach(btn => {
      btn.classList.remove('selected');
    });
  }
}

function executeFromBlock(button) {
  const copySection = button.closest('.copy-section');
  const query = copySection.querySelector('.copy-text')?.textContent.trim();
  const title = copySection.dataset.title || "Executing SQL Query";

  // Paste into SQL input and run the query
  document.getElementById('sqlInput').value = query;
  runQuery();

  // Set description
  document.querySelector('.sql-description').textContent = title;

  //toggleSidebar('sql');
  //toggleSidebar('info');

  toggleSidebar('sidebar-info');
  toggleSidebar('sidebar-sql');
  //toggleSidebar('sidebar-register');
}

/*function showRegistrationStatusMessage(text, isSuccess) {
  const registerStatusDiv = document.getElementById('registerStatus');
  registerStatusDiv.textContent = text;
  registerStatusDiv.style.display = 'block';

  if (isSuccess) {
    registerStatusDiv.classList.add('success');
    registerStatusDiv.classList.remove('error');
  } else {
    registerStatusDiv.classList.add('error');
    registerStatusDiv.classList.remove('success');
  }
}*/

function showStatusMessage(elementId, text, isSuccess) {
  const statusDiv = document.getElementById(elementId);
  if (!statusDiv) return;

  statusDiv.textContent = text;
  statusDiv.style.display = 'block';
  statusDiv.classList.toggle('success', isSuccess);
  statusDiv.classList.toggle('error', !isSuccess);
}

document.getElementById('registerForm').addEventListener('submit', function (e) {
  e.preventDefault();

  const username = document.getElementById("username").value;
  const referralCode = document.getElementById("referralSelect").value;

  fetch("/api/v1/users/register", {
    method: "POST",
    headers: {
      "Content-Type": "application/json"
    },
    body: JSON.stringify({
      username: username,
      referralCode: referralCode
    })
  })
    .then(async response => {
      const data = await response.json();
      if (!response.ok) throw new Error(data.message || "Registration failed");
      return data;
    })
    .then(data => {
      //alert("Registration successful");
      console.log(data); // Optional: show success data
      //showRegistrationStatusMessage("Registration successful!", true);
      showStatusMessage("registerStatus", "Registration successful!", true);
      loadTree();
    })
    .catch(error => {
      //showRegistrationStatusMessage(error.message, false);
      showStatusMessage("registerStatus", error.message, false);
      console.log("Error: ", error);
    });
});


document.getElementById('depositForm').addEventListener('submit', function (e) {
  e.preventDefault();
  const body = {
    userId: document.getElementById('userSelect').value,
    amount: parseFloat(document.getElementById('amount').value),
    transactionType: document.getElementById('transactionType').value,
    remarks: document.getElementById('remarks').value
  };

  fetch("/api/v1/users/deposit", {
    method: "POST",
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(body)
  })
    .then(async response => {
      const data = await response.json();
      if (!response.ok) throw new Error(data.message || "Registration failed");
      return data;
    })
    .then(data => {
      //alert("Registration successful");
      console.log(data); // Optional: show success data
      showStatusMessage("depositStatus", "Deposit successful!", true);
    })
    .catch(error => {
      showStatusMessage("depositStatus", error.message, false);
      console.log("Error: ", error);
    });
});

async function scheduleIncome() {
  try {
    const response = await fetch('/api/v1/bonus/referral/schedule');
    const responseData = await response.json();
    console.log("RESPONSE: ", responseData);
    alert(JSON.stringify(responseData));
  } catch (error) {
    console.error("Error scheduling income:", error);
    alert("Failed to schedule income.");
  }
}

function purchaseProduct(button, event) {
  const productId = button.dataset.id;

  const body = {
    userId: selectedUserId,
    productId: productId
  };

  fetch("/api/v1/products/purchase", {
    method: "POST",
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(body)
  })
  .then(async response => {
    const data = await response.json();
    if (!response.ok) throw new Error(data.message || "Purchase failed");
    return data;
  })
  .then(data => {
    //alert("Registration successful");
    loadProducts();
    console.log("PURCHASE_RESPONSE: ", data);
    showStatusMessage("purchaseStatus", "Purchase successful!", true);
  })
  .catch(error => {
    showStatusMessage("purchaseStatus", error.message, false);
    console.log("Error: ", error);
  });
}

function sellProduct(button, event) {
  const productId = button.dataset.id;

  const body = {
    userId: selectedUserId,
    productId: productId
  };

  fetch("/api/v1/products/sell", {
    method: "POST",
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(body)
  })
  .then(async response => {
    const data = await response.json();
    if (!response.ok) throw new Error(data.message || "Sell failed");
    return data;
  })
  .then(data => {
    //alert("Registration successful");
    loadProducts();
    console.log("SELL_RESPONSE: ", data);
    showStatusMessage("purchaseStatus", "Sell successful!", true);
  })
  .catch(error => {
    showStatusMessage("purchaseStatus", error.message, false);
    console.log("Error: ", error);
  });
}