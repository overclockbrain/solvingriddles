// DOM（画面）が全部読み込まれてから動くようにする
document.addEventListener('DOMContentLoaded', function () {

    // ボタンとメニューの要素を取得
    const menuBtn = document.getElementById('menu-btn');
    const sideMenu = document.getElementById('side-menu');

    // ボタンが押された時の動き
    menuBtn.addEventListener('click', function () {
        // メニューの表示/非表示を切り替え (toggle)
        if (sideMenu.style.display === 'none' || sideMenu.style.display === '') {
            sideMenu.style.display = 'block'; // 表示
            menuBtn.textContent = '×';       // ボタンをバツ印に
        } else {
            sideMenu.style.display = 'none';  // 非表示
            menuBtn.textContent = '≡';       // ボタンを三本線に
        }
    });
});