async function loadIncome() {
    const userId = selectedUserId;
    if (!userId) return alert("Please enter a user ID.");

    const url = `/api/v1/income/${userId}/summary`; // Adjust base path if needed

    try {
        const res = await fetch(url);
        if (!res.ok) throw new Error("Failed to fetch income data");

        const data = await res.json();
        const tbody = document.getElementById("incomeTable").getElementsByTagName("tbody")[0];
        tbody.innerHTML = "";

        data.forEach(row => {
            const tr = document.createElement("tr");

            const tdType = document.createElement("td");
            tdType.textContent = row.type;

            const tdDaily = document.createElement("td");
            tdDaily.textContent = row.dailyIncome.toFixed(2);

            const tdTotal = document.createElement("td");
            tdTotal.textContent = row.totalIncome.toFixed(2);

            tr.appendChild(tdType);
            tr.appendChild(tdDaily);
            tr.appendChild(tdTotal);

            tbody.appendChild(tr);
        });

    } catch (err) {
        alert("Error: " + err.message);
        console.error(err);
    }
}