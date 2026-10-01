// Theme Management (Dark / Light Mode with localStorage Persistence)
(function () {
    // Apply saved theme immediately before DOM render to avoid flash of wrong theme
    const savedTheme = localStorage.getItem('theme') || 'dark';
    document.documentElement.setAttribute('data-theme', savedTheme);
})();

function toggleTheme() {
    const currentTheme = document.documentElement.getAttribute('data-theme') || 'dark';
    const newTheme = currentTheme === 'dark' ? 'light' : 'dark';
    document.documentElement.setAttribute('data-theme', newTheme);
    localStorage.setItem('theme', newTheme);
    updateThemeIcons(newTheme);

    // Notify listeners (e.g. Chart.js) of the theme change
    window.dispatchEvent(new CustomEvent('themeChanged', { detail: { theme: newTheme } }));
}

function updateThemeIcons(theme) {
    document.querySelectorAll('.theme-toggle-btn').forEach(function (btn) {
        const icon = btn.querySelector('i');
        if (icon) {
            if (theme === 'light') {
                icon.className = 'bi bi-moon-stars-fill';
                btn.title = 'Switch to Dark Mode';
                btn.setAttribute('aria-label', 'Switch to Dark Mode');
            } else {
                icon.className = 'bi bi-sun-fill';
                btn.title = 'Switch to Light Mode';
                btn.setAttribute('aria-label', 'Switch to Light Mode');
            }
        }
    });
}

document.addEventListener('DOMContentLoaded', function () {
    const currentTheme = document.documentElement.getAttribute('data-theme') || 'dark';
    updateThemeIcons(currentTheme);

    document.querySelectorAll('.theme-toggle-btn').forEach(function (btn) {
        // Prevent duplicate listener binding
        btn.removeEventListener('click', toggleTheme);
        btn.addEventListener('click', toggleTheme);
    });
});
