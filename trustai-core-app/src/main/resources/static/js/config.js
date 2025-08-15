
const configState = {}; // current values
const defaultConfigState = {}; // original values from server
const configBaseUrl = "http://localhost:8888";

function renderConfigs(data) {
  const container = document.getElementById('configContainer');
  container.innerHTML = '';

  data.forEach(({ key, value, valueType, enumValues }) => {
    const div = document.createElement('div');
    div.className = 'config-item';

    const label = document.createElement('label');
    label.textContent = key;

    let inputElement;

    // Handle boolean as toggle
    if (valueType === 'BOOLEAN' || value === 'true' || value === 'false') {
      // Boolean toggle
      inputElement = document.createElement('input');
      inputElement.type = 'checkbox';
      inputElement.checked = (value === 'true');
      inputElement.onchange = () => configState[key] = inputElement.checked.toString();
    }

    // Handle enum as dropdown
    else if (enumValues && enumValues.trim().length > 0) {
      inputElement = document.createElement('select');

      enumValues.split(',').map(e => e.trim()).forEach(optionValue => {
        const option = document.createElement('option');
        option.value = optionValue;
        option.textContent = optionValue;
        if (optionValue === value) option.selected = true;
        inputElement.appendChild(option);
      });

      inputElement.onchange = () => configState[key] = inputElement.value;
    }

    // Number → Numeric input
    else if (['INT', 'FLOAT', 'DOUBLE', 'BIG_DECIMAL'].includes(valueType)) {
      inputElement = document.createElement('input');
      inputElement.type = 'number';
      inputElement.value = value;
      inputElement.step = valueType === 'INT' ? '1' : 'any';
      inputElement.oninput = () => configState[key] = inputElement.value;
    }

    // Handle everything else as text input
    else {
      // Input for numbers and strings
      inputElement = document.createElement('input');
      inputElement.type = 'text';
      inputElement.value = value;
      inputElement.oninput = () => configState[key] = input.value;
    }

    // Initial state
    configState[key] = value;
    defaultConfigState[key] = value; // Store original value for diffing

    div.appendChild(label);
    div.appendChild(inputElement);
    container.appendChild(div);
  });
}

async function loadConfigs() {
  const response = await fetch(`${configBaseUrl}/api/v1/configs`);
  const data = await response.json();
  renderConfigs(data);
}

async function updateConfigs() {
  console.log("UPDATE.....");
  const changedConfigs = Object.entries(configState)
    .filter(([key, value]) => value !== defaultConfigState[key])
    .map(([key, value]) => ({ key, value }));

  if (changedConfigs.length === 0) {
    alert('No changes to update.');
    return;
  }

  console.log("changedConfigs: ", changedConfigs);


  const response = await fetch(`${configBaseUrl}/api/v1/configs/update`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(changedConfigs)
  });

  if (response.ok) {
    alert('Configs updated successfully');
    changedConfigs.forEach(({ key, value }) => defaultConfigState[key] = value);
  } else {
    alert('Failed to update configs');
  }
}


document.getElementById('reloadConfig').addEventListener('click', async () => {
  try {
    const response = await fetch('/api/v1/configs/reload', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' }
    });
    if (response.ok) {
      alert('Configs loaded successfully');
    } else {
      alert('Failed to update configs');
    }
  } catch (error) {
    alert('Request failed: ' + error.message);
  }
});

loadConfigs();
