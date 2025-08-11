const incomeConfigBaseUrl = '/api/v1/income/configs';
let rankConfigData = []; // Store current loaded data for update reference
let teamConfigRaw  = [];
let rankHasUnsavedChanges = false;
let teamHasUnsavedChanges = false;
let pivotedData = {
  1: {}, // A
  2: {}, // B
  3: {}  // C
};

const ranks = ["RANK_2", "RANK_3", "RANK_4", "RANK_5"];

function renderRankConfigTable() {
  console.log("renderRankConfigTable.....");
  const tbody = document.querySelector('#rankTable tbody');
  tbody.innerHTML = '';

  rankConfigData.forEach(rankConfig => {
    const tr = document.createElement('tr');

    const tdRank = document.createElement('td');
    tdRank.textContent = rankConfig.rank;
    tr.appendChild(tdRank);

    const tdMin = document.createElement('td');
    const inputMin = document.createElement('input');
    inputMin.type = 'number';
    inputMin.value = rankConfig.minWalletBalance;
    inputMin.dataset.rank = rankConfig.rank;
    inputMin.dataset.field = 'minWalletBalance';
    inputMin.addEventListener('input', onRankInputChange);
    tdMin.appendChild(inputMin);
    tr.appendChild(tdMin);

    const tdMax = document.createElement('td');
    const inputMax = document.createElement('input');
    inputMax.type = 'number';
    inputMax.value = rankConfig.maxWalletBalance;
    inputMax.dataset.rank = rankConfig.rank;
    inputMax.dataset.field = 'maxWalletBalance';
    inputMax.addEventListener('input', onRankInputChange);
    tdMax.appendChild(inputMax);
    tr.appendChild(tdMax);

    const tdTxn = document.createElement('td');
    const inputTxn = document.createElement('input');
    inputTxn.type = 'number';
    inputTxn.value = rankConfig.txnPerDay;
    inputTxn.dataset.rank = rankConfig.rank;
    inputTxn.dataset.field = 'txnPerDay';
    inputTxn.addEventListener('input', onRankInputChange);
    tdTxn.appendChild(inputTxn);
    tr.appendChild(tdTxn);

    for (let level = 1; level <= 3; level++) {
      const tdLevel = document.createElement('td');
      const inputLevel = document.createElement('input');
      inputLevel.type = 'number';
      inputLevel.min = 0;
      inputLevel.value = rankConfig.requiredLevelCounts?.[level] || 0;
      inputLevel.dataset.rank = rankConfig.rank;
      inputLevel.dataset.field = `requiredLevelCounts.${level}`;
      inputLevel.addEventListener('input', onRankInputChange);
      tdLevel.appendChild(inputLevel);
      tr.appendChild(tdLevel);
    }

    const tdProfit = document.createElement('td');
    const inputProfit = document.createElement('input');
    inputProfit.type = 'number';
    inputProfit.value = rankConfig.commissionRate;
    inputProfit.dataset.rank = rankConfig.rank;
    inputProfit.dataset.field = 'commissionRate';
    inputProfit.addEventListener('input', onRankInputChange);
    tdProfit.appendChild(inputProfit);
    tr.appendChild(tdProfit);

    const tdStake = document.createElement('td');
    const inputStake = document.createElement('input');
    inputStake.type = 'number';
    inputStake.value = rankConfig.stakeValue;
    inputStake.dataset.rank = rankConfig.rank;
    inputStake.dataset.field = 'stakeValue';
    inputStake.addEventListener('input', onRankInputChange);
    tdStake.appendChild(inputStake);
    tr.appendChild(tdStake);


    tbody.appendChild(tr);
  });
}


function renderTeamTable() {
  console.log("renderTeamTable.......");
  const tbody = document.querySelector('#teamConfigTable tbody');
  tbody.innerHTML = '';

  teamConfigRaw.forEach(config => {
  // teamConfigData.forEach(config => {
    const tr = document.createElement('tr');

    const tdRank = document.createElement('td');
    tdRank.textContent = config.rank;
    tr.appendChild(tdRank);

    for (let level = 1; level <= 3; level++) {
      const td = document.createElement('td');
      const input = document.createElement('input');
      input.type = 'number';
      input.step = '0.01';
      input.min = 0;
      input.value = config.incomePercentages?.[level] || 0;
      input.dataset.rank = config.rank;
      input.dataset.level = level;
      input.addEventListener('input', onTeamInputChange);
      td.appendChild(input);
      tr.appendChild(td);
    }

    tbody.appendChild(tr);
  });
}


function renderPivotTable() {
  console.log("renderPivotTable.......");
  const tbody = document.querySelector('#teamIncomeTable tbody');
  tbody.innerHTML = '';

  //const levelLabels = { 1: 'Lv.A', 2: 'Lv.B', 3: 'Lv.C' };
  const levelLabels = { 1: 'Lv.A / Depth-1', 2: 'Lv.B / Depth-2', 3: 'Lv.C / Depth-3' };

  for (let level = 1; level <= 3; level++) {
    const tr = document.createElement('tr');

    const tdLabel = document.createElement('td');
    tdLabel.textContent = levelLabels[level];
    tr.appendChild(tdLabel);

    for (const rank of ranks) {
      const td = document.createElement('td');
      const input = document.createElement('input');
      input.type = 'number';
      input.min = 0;
      input.step = '0.01';
      input.value = pivotedData[level][rank] ?? 0;
      input.dataset.rank = rank;
      input.dataset.level = level;
      input.addEventListener('input', onPivotInputChange);
      td.appendChild(input);
      tr.appendChild(td);
    }

    tbody.appendChild(tr);
  }
}


async function loadRankConfigData() {
  console.log("loadRankConfigData.......");
  const resp = await fetch(`${incomeConfigBaseUrl}/rank`);
  rankConfigData = await resp.json();
  console.log("RANK_CONFIG_DATA: ", rankConfigData);
  renderRankConfigTable();
  document.getElementById('updateBtn').disabled = true;
  rankHasUnsavedChanges = false;
}

async function loadTeamRebateConfig() {
  console.log("loadTeamRebateConfig........");
  const resp = await fetch(`${incomeConfigBaseUrl}`);
  teamConfigRaw = await resp.json();
  console.log("TEAM_CONFIG_RAW: ", teamConfigRaw);
  pivotTeamData();
  //renderTeamTable();
  renderPivotTable();
  document.getElementById('updateTeamBtn').disabled = true;
  teamHasUnsavedChanges = false;
}

function pivotTeamData() {
  console.log("pivotTeamData........");
  teamConfigRaw.forEach(entry => {
    const rank = entry.rank;
    if (!entry.incomePercentages) return; // skip if null/undefined
    Object.entries(entry.incomePercentages).forEach(([level, value]) => {
      if (!pivotedData[level]) {
        pivotedData[level] = {};
      }
      pivotedData[level][rank] = value;
    });
  });
}

function onRankInputChange(event) {
  const input = event.target;
  const rank = input.dataset.rank;
  const field = input.dataset.field;
  const value = parseInt(input.value);

  const rankConfig = rankConfigData.find(r => r.rank === rank);
  if (!rankConfig) return;

  let isChanged = false;

  if (field === 'minWalletBalance') {
    isChanged = rankConfig.minWalletBalance !== value;
    rankConfig.minWalletBalance = value;
  } else if (field === 'maxWalletBalance') {
    isChanged = rankConfig.maxWalletBalance !== value;
    rankConfig.maxWalletBalance = value;
  } else if (field === 'commissionRate') {
    isChanged = rankConfig.commissionRate !== value;
    rankConfig.commissionRate = value;
  } else if (field.startsWith('requiredLevelCounts.')) {
    const level = parseInt(field.split('.')[1]);
    if (!rankConfig.requiredLevelCounts) rankConfig.requiredLevelCounts = {};
    isChanged = rankConfig.requiredLevelCounts[level] !== value;
    rankConfig.requiredLevelCounts[level] = value;
  }

  // Highlight if changed
  if (isChanged) {
    input.classList.add('updated');
    rankHasUnsavedChanges = true;
    document.getElementById('updateBtn').disabled = false;
  } else {
    input.classList.remove('updated');
  }
}

function onPivotInputChange(event) {
  console.log("onPivotInputChange........");
  
  const input = event.target;
  const { rank, level } = event.target.dataset;
  const value = parseFloat(event.target.value) || 0;

  // Update the current state
  pivotedData[level][rank] = value;

  // Check original value from teamConfigRaw
  const original = teamConfigRaw.find(e => e.rank === rank);
  const originalValue = original?.incomePercentages?.[level] ?? 0;

  if (value !== originalValue) {
    input.classList.add('updated');
    teamHasUnsavedChanges = true;
    document.getElementById('updateTeamBtn').disabled = false;
  } else {
    input.classList.remove('updated');
  }
}

function unpivotData() {
  console.log("unpivotData........");
  const output = ranks.map(rank => ({
    rank,
    incomePercentages: {
      1: pivotedData[1][rank] || 0,
      2: pivotedData[2][rank] || 0,
      3: pivotedData[3][rank] || 0
    }
  }));
  return output;
}

async function updateRankConfig() {
  console.log("UPDATE: ", rankConfigData);

  const alertBox = document.querySelector('.alert');
  alertBox.classList.add('hidden');
  alertBox.textContent = '';

  try {
    const resp = await fetch(`${incomeConfigBaseUrl}/rank`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(rankConfigData)
    });


    if (!resp.ok) {
      const errorText = await resp.text();
      alertBox.textContent = errorText || 'Update failed.';
      alertBox.className = 'alert error';
      return;
    }

    alertBox.textContent = 'Updated successfully!';
    alertBox.className = 'alert success';
    rankHasUnsavedChanges = false;
    document.getElementById('updateBtn').disabled = true;
    document.querySelectorAll('.updated').forEach(el => el.classList.remove('updated'));
  } catch (e) {
    alertBox.textContent = 'Server error: ' + e.message;
    alertBox.className = 'alert error';
  }
}


async function updateTeamData() {
  console.log("updateTeamData........");
  const dataToSend = unpivotData();
  console.log("REQUEST_PAYLOAD: ", dataToSend);
  
  const resp = await fetch(`${incomeConfigBaseUrl}/team`, {
    method: "PUT",
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(dataToSend)
  });

  if (resp.ok) {
    alert("Update successful");
    document.getElementById('updateTeamBtn').disabled = true;
    teamHasUnsavedChanges = false;

    // Clear highlights after update
    document.querySelectorAll('#teamIncomeTable input.updated').forEach(input => {
      input.classList.remove('updated');
    });

  } else {
    alert("Update failed");
  }
}

async function addNewRankConfig() {
  const newRankName = prompt("Enter new rank name (e.g., RANK_5):");
  if (!newRankName) return;
  if (rankConfigData.find(r => r.rank === newRankName)) {
    alert("Rank already exists!");
    return;
  }

  const newRank = {
    rank: newRankName,
    minWalletBalance: 0,
    maxWalletBalance: 0,
    commissionRate: 0,
    requiredLevelCounts: {
      1: 0,
      2: 0,
      3: 0
    }
  };

  rankConfigData.push(newRank);
  renderRankConfigTable(); // reload UI
}

document.getElementById('updateBtn').addEventListener('click', updateRankConfig);
document.getElementById('addRankBtn').addEventListener('click', addNewRankConfig);
document.getElementById('updateTeamBtn').addEventListener('click', updateTeamData);

window.onload = loadRankConfigData();
window.onload = loadTeamRebateConfig();