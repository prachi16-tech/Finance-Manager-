// Analytics & Chart.js Visualizations
document.addEventListener('DOMContentLoaded', function () {
    if (typeof rawAnalyticsData === 'undefined' || !rawAnalyticsData) {
        console.warn('No analytics data available for charts.');
        return;
    }

    const data = rawAnalyticsData;
    const labels = data.monthLabels || [];
    const incomeData = data.incomeTrend || [];
    const expenseData = data.expenseTrend || [];
    const savingsData = data.savingsTrend || [];
    const categoryLabels = data.categoryLabels || [];
    const categoryAmounts = data.categoryAmounts || [];

    // Chart.js Default Dark Theme Setup
    Chart.defaults.color = '#94a3b8';
    Chart.defaults.font.family = "'Plus Jakarta Sans', sans-serif";
    Chart.defaults.font.size = 12;

    const gridConfig = {
        color: 'rgba(255, 255, 255, 0.06)',
        drawBorder: false
    };

    // 1. Income vs Expenses Bar Chart
    const ctxIncomeVsExpense = document.getElementById('incomeVsExpenseChart');
    if (ctxIncomeVsExpense) {
        new Chart(ctxIncomeVsExpense, {
            type: 'bar',
            data: {
                labels: labels,
                datasets: [
                    {
                        label: 'Income (₹)',
                        data: incomeData,
                        backgroundColor: 'rgba(16, 185, 129, 0.85)',
                        borderRadius: 6,
                        borderSkipped: false
                    },
                    {
                        label: 'Expense (₹)',
                        data: expenseData,
                        backgroundColor: 'rgba(244, 63, 94, 0.85)',
                        borderRadius: 6,
                        borderSkipped: false
                    }
                ]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: { position: 'top', labels: { boxWidth: 12, padding: 16 } },
                    tooltip: {
                        callbacks: {
                            label: function (context) {
                                return context.dataset.label + ': ₹' + Number(context.raw).toLocaleString();
                            }
                        }
                    }
                },
                scales: {
                    x: { grid: gridConfig },
                    y: {
                        grid: gridConfig,
                        ticks: {
                            callback: function (val) { return '₹' + Number(val).toLocaleString(); }
                        }
                    }
                }
            }
        });
    }

    // 2. Category-wise Expenses Doughnut Chart
    const ctxCategory = document.getElementById('categoryDoughnutChart');
    if (ctxCategory) {
        new Chart(ctxCategory, {
            type: 'doughnut',
            data: {
                labels: categoryLabels.length > 0 ? categoryLabels : ['No Expenses Yet'],
                datasets: [{
                    data: categoryAmounts.length > 0 ? categoryAmounts : [1],
                    backgroundColor: [
                        '#6366f1', '#ec4899', '#3b82f6', '#10b981', '#f59e0b',
                        '#8b5cf6', '#14b8a6', '#f43f5e', '#06b6d4', '#84cc16'
                    ],
                    borderWidth: 2,
                    borderColor: '#111827'
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                cutout: '70%',
                plugins: {
                    legend: { position: 'right', labels: { boxWidth: 12, padding: 12 } },
                    tooltip: {
                        callbacks: {
                            label: function (context) {
                                return context.label + ': ₹' + Number(context.raw).toLocaleString();
                            }
                        }
                    }
                }
            }
        });
    }

    // 3. Monthly Expenses Trend Bar Chart
    const ctxMonthlyExpense = document.getElementById('monthlyExpenseChart');
    if (ctxMonthlyExpense) {
        new Chart(ctxMonthlyExpense, {
            type: 'bar',
            data: {
                labels: labels,
                datasets: [{
                    label: 'Expenses (₹)',
                    data: expenseData,
                    backgroundColor: 'rgba(239, 68, 68, 0.75)',
                    borderRadius: 8,
                    borderSkipped: false
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: { display: false },
                    tooltip: {
                        callbacks: {
                            label: function (context) { return 'Expense: ₹' + Number(context.raw).toLocaleString(); }
                        }
                    }
                },
                scales: {
                    x: { grid: gridConfig },
                    y: {
                        grid: gridConfig,
                        ticks: { callback: function (val) { return '₹' + Number(val).toLocaleString(); } }
                    }
                }
            }
        });
    }

    // 4. Savings Trend Line Chart
    const ctxSavingsTrend = document.getElementById('savingsTrendChart');
    if (ctxSavingsTrend) {
        new Chart(ctxSavingsTrend, {
            type: 'line',
            data: {
                labels: labels,
                datasets: [{
                    label: 'Net Savings (₹)',
                    data: savingsData,
                    borderColor: '#6366f1',
                    backgroundColor: 'rgba(99, 102, 241, 0.15)',
                    fill: true,
                    tension: 0.35,
                    pointBackgroundColor: '#818cf8',
                    pointRadius: 5,
                    pointHoverRadius: 7
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: { display: false },
                    tooltip: {
                        callbacks: {
                            label: function (context) { return 'Savings: ₹' + Number(context.raw).toLocaleString(); }
                        }
                    }
                },
                scales: {
                    x: { grid: gridConfig },
                    y: {
                        grid: gridConfig,
                        ticks: { callback: function (val) { return '₹' + Number(val).toLocaleString(); } }
                    }
                }
            }
        });
    }
});
