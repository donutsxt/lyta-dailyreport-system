/** 日報一覧画面の絞り込みボタンを押した時の処理 */
function search() {

    // 全レコードを表示
    for (let x = 0; x < listtable.rows.length; x++) {
        document.getElementById("listtable").rows[x].style.display = "table-row";
    }

    var inputValue;
    // 入力文字を取得する
    inputValue = document.getElementById("inputValue").value;

    var listValue;
    var displayflag;
    // 行の繰り返し
    for (let x = 0; x < listtable.rows.length; x++) {
        displayflag = "OFF";
        // 列の繰り返し
        for (let y = 0; y < listtable.rows[x].cells.length - 1; y++) {
            listValue = listtable.rows[x].cells[y].innerHTML;
            // 入力文字が含まれていたら変数を"ON"で上書き
            if (listValue.indexOf(inputValue, 0) >= 0) {
                displayflag = "ON";
            };
        };

        // 1行目は非表示処理をスキップ
        if(x != 0){
            // 変数が"ON"じゃない場合
            if (displayflag == "OFF"){
                //レコードを非表示
                document.getElementById("listtable").rows[x].style.display = "none";
            };
        }
    };
};
