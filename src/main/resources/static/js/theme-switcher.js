function toggleCSS() {
    var themeStylesheet = document.getElementById('theme-stylesheet');
    var currentThemeHref = themeStylesheet.getAttribute('href');
    var imageElement = document.getElementById('logo');
    var newThemeHref, newImageSrc, newButtonText;

    if (currentThemeHref.includes('light')) {
        newThemeHref = currentThemeHref.replace('light', 'dark');
        newImageSrc = '/png/logo-2-white.png';
        localStorage.setItem('theme', 'dark');
        localStorage.setItem('logo', newImageSrc);
    } else {
        newThemeHref = currentThemeHref.replace('dark', 'light');
        newImageSrc = '/png/logo-2.png';
        localStorage.setItem('theme', 'light');
        localStorage.setItem('logo', newImageSrc);
    }

    themeStylesheet.setAttribute('href', newThemeHref);
    imageElement.src = newImageSrc;
}

// Funkcja do ładowania motywu
function loadTheme() {
    var themeStylesheet = document.getElementById('theme-stylesheet');
    var imageElement = document.getElementById('logo');
    var storedTheme = localStorage.getItem('theme');
    var storedLogo = localStorage.getItem('logo');

    if (storedTheme && storedLogo) {
        themeStylesheet.setAttribute('href', themeStylesheet.getAttribute('href').replace(/light|dark/, storedTheme));
        imageElement.src = storedLogo;
    }
}

// Wywołanie funkcji loadTheme podczas ładowania każdej strony
document.addEventListener('DOMContentLoaded', loadTheme);
