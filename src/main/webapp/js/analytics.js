// Analytics & Chart.js Visualizations (Theme-Aware)
document.addEventListener('DOMContentLoaded', function () {
    const payloadElem = document.getElementById('analyticsPayload');
    if (!payloadElem) {
        return;
    }

    let data = {};
    try {
        data = JSON.parse(payloadElem.getAttribute('data-analytics') || '{}');
    } catch (e) {
        console.warn('Could not parse analytics payload', e);
        return;
    }

    const labels = data.monthLabels || [];
    const incomeData = data.incomeTrend || [];
    const expenseData = data.expenseTrend || [];
    const savingsData = data.savingsTrend || [];
    const categoryLabels = data.categoryLabels || [];
    const categoryAmounts = data.categoryAmounts || [];

    let chartInstances = [];

    function renderCharts() {
        // Destroy existing chart instances before re-rendering
        chartInstances.forEach(chart => chart.destroy());
        chartInstances = [];

        const isLight = document.documentElement.getAttribute('data-theme') === 'light';
        const textColor = isLight ? '#4b5563' : '#94a3b8';
        const gridColor = isLight ? 'rgba(0, 0, 0, 0.06)' : 'rgba(255, 255, 255, 0.06)';
        const doughnutBorder = isLight ? '#ffffff' : '#111827';

        Chart.defaults.color = textColor;
        Chart.defaults.font.family = "'Plus Jakarta Sans', sans-serif";
        Chart.defaults.font.size = 12;

        const gridConfig = {
            color: gridColor,
            drawBorder: false
        };

        // 1. Income vs Expenses Bar Chart
        const ctxIncomeVsExpense = document.getElementById('incomeVsExpenseChart');
        if (ctxIncomeVsExpense) {
            const chart1 = new Chart(ctxIncomeVsExpense, {
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
                        legend: { position: 'top', labels: { boxWidth: 12, padding: 16, color: textColor } },
                        tooltip: {
                            callbacks: {
                                label: function (context) {
                                    return context.dataset.label + ': ₹' + Number(context.raw).toLocaleString();
                                }
                            }
                        }
                    },
                    scales: {
                        x: { grid: gridConfig, ticks: { color: textColor } },
                        y: {
                            grid: gridConfig,
                            ticks: {
                                color: textColor,
                                callback: function (val) { return '₹' + Number(val).toLocaleString(); }
                            }
                        }
                    }
                }
            });
            chartInstances.push(chart1);
        }

        // 2. Category-wise Expenses Doughnut Chart
        const ctxCategory = document.getElementById('categoryDoughnutChart');
        if (ctxCategory) {
            const chart2 = new Chart(ctxCategory, {
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
                        borderColor: doughnutBorder
                    }]
                },
                options: {
                    responsive: true,
                    maintainAspectRatio: false,
                    cutout: '70%',
                    plugins: {
                        legend: { position: 'right', labels: { boxWidth: 12, padding: 12, color: textColor } },
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
            chartInstances.push(chart2);
        }

        // 3. Monthly Expenses Trend Bar Chart
        const ctxMonthlyExpense = document.getElementById('monthlyExpenseChart');
        if (ctxMonthlyExpense) {
            const chart3 = new Chart(ctxMonthlyExpense, {
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
                        x: { grid: gridConfig, ticks: { color: textColor } },
                        y: {
                            grid: gridConfig,
                            ticks: {
                                color: textColor,
                                callback: function (val) { return '₹' + Number(val).toLocaleString(); }
                            }
                        }
                    }
                }
            });
            chartInstances.push(chart3);
        }

        // 4. Savings Trend Line Chart
        const ctxSavingsTrend = document.getElementById('savingsTrendChart');
        if (ctxSavingsTrend) {
            const chart4 = new Chart(ctxSavingsTrend, {
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
                        x: { grid: gridConfig, ticks: { color: textColor } },
                        y: {
                            grid: gridConfig,
                            ticks: {
                                color: textColor,
                                callback: function (val) { return '₹' + Number(val).toLocaleString(); }
                            }
                        }
                    }
                }
            });
            chartInstances.push(chart4);
        }
    }

    // Initial render
    renderCharts();

    // Re-render when theme changes
    window.addEventListener('themeChanged', function () {
        renderCharts();
    });
});
