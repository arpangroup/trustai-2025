let rankData = [];

function loadProducts() {
    if (!selectedUserId) return alert("Please enter a user ID.");

  const baseUrl = '/api/v1/products';
  const url = (selectedUserId && selectedUserId !== 1) ? `${baseUrl}?userId=${selectedUserId}` : baseUrl;

  fetch(url)
    .then(response => response.json())
    .then(data => {
      console.log("PRODUCTS: ", data);
      const tbody = document.querySelector('#productsTable tbody');
      tbody.innerHTML = ''; // Clear previous rows

      data.content.forEach(product => {
      const tr = document.createElement('tr');

      let actionButtons = '';
      const { purchased, transactionStatus, id } = product;

      if (purchased) {
        actionButtons = `<button data-id="${id}" onClick="sellProduct(this, event)">Sell</button>`;
      } else {
        actionButtons = `<button data-id="${id}" onClick="purchaseProduct(this, event)">Purchase</button>`;
      }

      tr.innerHTML = `
        <td>${product.id}</td>
        <td>${product.name}</td>
        <td>${product.price}</td>
        <td>${actionButtons}</td>
      `;

      tbody.appendChild(tr);
      });
    })
    .catch(error => console.error('Error:', error));
}


async function loadRanks() {//selectedUserRank
    const res = await fetch('/api/v1/config/income/rank');
    rankData = await res.json();

    const rankSelect = document.getElementById('rankSelect');
    const rangeSelect = document.getElementById('rangeSelect');

    // ✅ Clear previous options
    rankSelect.innerHTML = '<option value="">Level &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; Income%</option>';
    rangeSelect.innerHTML = '<option value=""></option>';
    rangeSelect.disabled = true;

    console.log("selectedUserRank: ", selectedUserRank);
    console.log("RANK_DATA: ", rankData);
    rankData.forEach((rank, index) => {
        const nextRank = rankData[index + 1];
        const currentRate = parseFloat(rank.commissionRate).toFixed(1);
        const nextRate = nextRank ? parseFloat(nextRank.commissionRate).toFixed(1) : currentRate;

        const label = `${rank.rank} \u00A0\u00A0\u00A0\u00A0 ${currentRate}% - ${nextRate}%`;

        const option = document.createElement('option');
        option.value = rank.rank;
        //option.textContent = `${rank.rank} - ${rank.commissionRate}%`;
        option.textContent = label;

        if (rank.rank == selectedUserRank) {
            option.selected = true;
        } else {
            //option.disabled = true;
        }

        //rankSelect.appendChild(option);
        rankSelect.add(option);
    });
    // Automatically trigger range dropdown selection
    updateRangeDropdown();
}

function updateRangeDropdown() {
    const selectedRank = document.getElementById('rankSelect').value;
    const rangeSelect = document.getElementById('rangeSelect');
    rangeSelect.innerHTML = ''; // clear previous

    if (!selectedRank) {
        rangeSelect.disabled = true;
        return;
    }

    //            const rank = rankData.find(r => r.rank === selectedRank);
    //            const option = document.createElement('option');
    //            option.value = `${rank.minWalletBalance}-${rank.maxWalletBalance}`;
    //            option.textContent = `${rank.minWalletBalance} - ${rank.maxWalletBalance}`;
    //            option.selected = true;
    //            rangeSelect.appendChild(option);

    rankData.forEach(rank => {
        const option = document.createElement('option');
        option.value = `${rank.minWalletBalance}-${rank.maxWalletBalance}`;
        option.textContent = `${rank.minWalletBalance} - ${rank.maxWalletBalance}`;

        // Enable only the selected rank's option
        option.disabled = rank.rank !== selectedRank;
        option.selected = rank.rank === selectedRank;

        rangeSelect.appendChild(option);
    });

    rangeSelect.disabled = false;
}

document.getElementById('confirmReserve').addEventListener('click', async  () => {
    console.log("confirmReserve for userId: ", selectedUserId);
    if (!selectedUserId) {
        alert("Please select a user first");
        return;
    }

    /*const rankSelect = document.getElementById('rankSelect');
    const selectedRank = rankSelect.value;
    if (!selectedRank) {
        alert("Please select a rank before confirming reservation.");
        rankSelect.focus();
        return;
    }*/


    try {
        const resp = await fetch(`/api/v1/products/reserve/${selectedUserId}`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' }
        });
        console.log("PURCHASE_RESPONSE: ", resp);

        if (!resp.ok) {
            const errorText = await resp.text();
            showStatusMessage("purchaseStatus", errorText, false);
            return;
        }
        showStatusMessage("purchaseStatus", "Purchase successful!", true);
    } catch (e) {
        console.log("PURCHASE_EXCEPTION: ", e);
        showStatusMessage("purchaseStatus", e.message, false);
    }
});

// 👇 Call this after page load
//window.onload = loadRanks;