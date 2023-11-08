function toggleCSS() {
    var themeStylesheet = document.getElementById('theme-stylesheet');
    var currentThemeHref = themeStylesheet.getAttribute('href');
    var imageElement = document.getElementById('logo');
    var themeButton = document.getElementById('theme-button');
    var newThemeHref, newImageSrc, newButtonText;

    if (currentThemeHref.includes('light')) {
        newThemeHref = currentThemeHref.replace('light', 'dark');
        newImageSrc = '/png/logo-2-white.png';
        newButtonText = 'Jasny motyw';
        localStorage.setItem('theme', 'dark');
        localStorage.setItem('logo', newImageSrc);
    } else {
        newThemeHref = currentThemeHref.replace('dark', 'light');
        newImageSrc = '/png/logo-2.png';
        newButtonText = 'Ciemny motyw';
        localStorage.setItem('theme', 'light');
        localStorage.setItem('logo', newImageSrc);
    }

    themeStylesheet.setAttribute('href', newThemeHref);
    imageElement.src = newImageSrc;
    themeButton.textContent = newButtonText;
}

// Funkcja do ładowania motywu
function loadTheme() {
    var themeStylesheet = document.getElementById('theme-stylesheet');
    var imageElement = document.getElementById('logo');
    var themeButton = document.getElementById('theme-button');
    var storedTheme = localStorage.getItem('theme');
    var storedLogo = localStorage.getItem('logo');

    if (storedTheme && storedLogo) {
        themeStylesheet.setAttribute('href', themeStylesheet.getAttribute('href').replace(/light|dark/, storedTheme));
        imageElement.src = storedLogo;
        themeButton.textContent = storedTheme === 'dark' ? 'Jasny motyw' : 'Ciemny motyw';
    }
}

// Wywołanie funkcji loadTheme podczas ładowania każdej strony
document.addEventListener('DOMContentLoaded', loadTheme);
