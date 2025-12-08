/**
 * コンテンツが読み込まれている場合、ハンバーガーメニューの表示/非表示切り替え
 */
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

/**
 * 並べ替え問題のドラッグ＆ドロップ処理
 */
document.addEventListener("DOMContentLoaded", () => {
    const sortList = document.getElementById("sortable-list");

    // sortページじゃなければ何もしない（エラー防止）
    if (!sortList) return;

    let draggingItem = null;

    // 1. ドラッグ開始
    sortList.addEventListener("dragstart", (e) => {
        draggingItem = e.target;
        // ちょっと遅らせてクラスをつける（見た目の調整）
        setTimeout(() => e.target.classList.add("dragging"), 0);
    });

    // 2. ドラッグ終了
    sortList.addEventListener("dragend", (e) => {
        e.target.classList.remove("dragging");
        draggingItem = null;
    });

    // 3. ドラッグ中（並べ替えロジック）
    sortList.addEventListener("dragover", (e) => {
        e.preventDefault(); // これがないとドロップできない

        const afterElement = getDragAfterElement(sortList, e.clientY);
        if (afterElement == null) {
            sortList.appendChild(draggingItem);
        } else {
            sortList.insertBefore(draggingItem, afterElement);
        }
    });
});

/**
 * マウス位置から「どこの上に落とそうとしてるか」を判定する魔法の関数
 * @param {HTMLElement} container 並べ替え対象のリスト要素
 * @param {number} y マウスのY座標
 * @returns {HTMLElement|null} マウス位置の次に来る要素、なければ null
 */
function getDragAfterElement(container, y) {
    const draggableElements = [...container.querySelectorAll(".sort-item:not(.dragging)")];

    return draggableElements.reduce((closest, child) => {
        const box = child.getBoundingClientRect();
        const offset = y - box.top - box.height / 2; // 要素の中心からの距離

        if (offset < 0 && offset > closest.offset) {
            return { offset: offset, element: child };
        } else {
            return closest;
        }
    }, { offset: Number.NEGATIVE_INFINITY }).element;
}

/**
 * 並べ替えた結果を隠しフィールドにセットしてフォーム送信
 */
function submitSortAnswer() {
    const listItems = document.querySelectorAll(".sort-item");

    // リストの並び順通りに data-value を集めてカンマ区切りにする
    // 例: ["red", "green", "refactor"] -> "red,green,refactor"
    const answerArray = Array.from(listItems).map(item => item.getAttribute("data-value"));
    const finalAnswer = answerArray.join(",");

    // 隠しフィールドにセットして送信
    document.getElementById("hiddenAnswer").value = finalAnswer;
    document.getElementById("sortForm").submit();
}