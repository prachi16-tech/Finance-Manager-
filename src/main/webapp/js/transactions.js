// Transactions Page JavaScript (Modals, Fetch JSON for Edit, Validations)
document.addEventListener('DOMContentLoaded', function () {
    const addModal = document.getElementById('addTransactionModal');
    const editModal = document.getElementById('editTransactionModal');

    // Open Add Modal
    window.openAddModal = function () {
        if (addModal) {
            addModal.style.display = 'flex';
            // Set default date to today if empty
            const dateInput = document.getElementById('addTransactionDate');
            if (dateInput && !dateInput.value) {
                dateInput.value = new Date().toISOString().split('T')[0];
            }
        }
    };

    window.closeAddModal = function () {
        if (addModal) addModal.style.display = 'none';
    };

    // Open Edit Modal & Fetch Data
    window.openEditModal = function (transactionId) {
        if (!editModal) return;

        fetch('transactions?action=getJson&id=' + transactionId)
            .then(response => {
                if (!response.ok) throw new Error('Network error');
                return response.json();
            })
            .then(data => {
                document.getElementById('editId').value = data.id;
                document.getElementById('editType').value = data.type;
                document.getElementById('editAmount').value = data.amount;
                document.getElementById('editCategory').value = data.category;
                document.getElementById('editDescription').value = data.description;
                document.getElementById('editTransactionDate').value = data.transactionDate;
                document.getElementById('editPaymentMethod').value = data.paymentMethod;

                editModal.style.display = 'flex';
            })
            .catch(err => {
                alert('Could not fetch transaction details: ' + err.message);
            });
    };

    window.closeEditModal = function () {
        if (editModal) editModal.style.display = 'none';
    };

    // Close modals on clicking backdrop
    window.onclick = function (event) {
        if (event.target === addModal) addModal.style.display = 'none';
        if (event.target === editModal) editModal.style.display = 'none';
    };
});
