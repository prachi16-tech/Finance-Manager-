// Dashboard Interactions and UI enhancements
document.addEventListener('DOMContentLoaded', function () {
    // Sidebar toggle for mobile
    const menuToggle = document.getElementById('menuToggle');
    const sidebar = document.getElementById('sidebar');
    if (menuToggle && sidebar) {
        menuToggle.addEventListener('click', function () {
            sidebar.classList.toggle('active');
        });
    }

    // Initialize progress bars from data-width attribute
    document.querySelectorAll('.progress-bar[data-width]').forEach(function (bar) {
        bar.style.width = bar.getAttribute('data-width') + '%';
    });

    // Auto dismiss alert messages after 5 seconds
    const alerts = document.querySelectorAll('.alert');
    alerts.forEach(function (alert) {
        setTimeout(function () {
            alert.style.transition = 'opacity 0.5s ease';
            alert.style.opacity = '0';
            setTimeout(function () {
                alert.remove();
            }, 500);
        }, 5000);
    });
});
