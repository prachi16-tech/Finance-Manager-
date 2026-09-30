// Financial Goals Interactions
document.addEventListener('DOMContentLoaded', function () {
    const addGoalModal = document.getElementById('addGoalModal');
    const editGoalModal = document.getElementById('editGoalModal');
    const depositModal = document.getElementById('depositModal');

    window.openAddGoalModal = function () {
        if (addGoalModal) addGoalModal.style.display = 'flex';
    };

    window.closeAddGoalModal = function () {
        if (addGoalModal) addGoalModal.style.display = 'none';
    };

    window.openEditGoalModal = function (goalId) {
        if (!editGoalModal) return;

        fetch('goals?action=getJson&id=' + goalId)
            .then(res => res.json())
            .then(data => {
                document.getElementById('editGoalId').value = data.id;
                document.getElementById('editGoalName').value = data.goalName;
                document.getElementById('editTargetAmount').value = data.targetAmount;
                document.getElementById('editDeadline').value = data.deadline;
                editGoalModal.style.display = 'flex';
            })
            .catch(err => alert('Error fetching goal details: ' + err.message));
    };

    window.closeEditGoalModal = function () {
        if (editGoalModal) editGoalModal.style.display = 'none';
    };

    window.openDepositModal = function (goalId, goalName) {
        if (!depositModal) return;
        document.getElementById('depositGoalId').value = goalId;
        document.getElementById('depositGoalTitle').innerText = 'Add Savings to "' + goalName + '"';
        document.getElementById('depositAmount').value = '';
        depositModal.style.display = 'flex';
    };

    window.closeDepositModal = function () {
        if (depositModal) depositModal.style.display = 'none';
    };

    window.onclick = function (event) {
        if (event.target === addGoalModal) addGoalModal.style.display = 'none';
        if (event.target === editGoalModal) editGoalModal.style.display = 'none';
        if (event.target === depositModal) depositModal.style.display = 'none';
    };
});
