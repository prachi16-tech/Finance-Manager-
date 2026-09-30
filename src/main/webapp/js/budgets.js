// Budget Management Interactions
document.addEventListener('DOMContentLoaded', function () {
    const budgetModal = document.getElementById('budgetModal');

    window.openBudgetModal = function (category, amount) {
        if (!budgetModal) return;

        if (category) {
            document.getElementById('budgetCategory').value = category;
        }
        if (amount && amount > 0) {
            document.getElementById('budgetAmount').value = amount;
        } else {
            document.getElementById('budgetAmount').value = '';
        }

        budgetModal.style.display = 'flex';
    };

    window.closeBudgetModal = function () {
        if (budgetModal) budgetModal.style.display = 'none';
    };

    window.onclick = function (event) {
        if (event.target === budgetModal) budgetModal.style.display = 'none';
    };
});
