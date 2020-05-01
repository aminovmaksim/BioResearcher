let coll = document.getElementsByClassName("collapsible");

for (let i = 0; i < coll.length; i++) {
    coll[i].addEventListener("click", function () {
        this.classList.toggle("active");
        let content = this.nextElementSibling;
        if (content.style.maxHeight) {
            content.style.maxHeight = null;
        } else {
            content.style.maxHeight = content.scrollHeight + "px";
        }
    });
}

let chart_el = document.getElementById('chart');
let ctx = chart_el.getContext('2d');
let chart = new Chart(document.getElementById("chart"), {
    type: "line"
});

let tests_global = [];

get_tests_from_cookie();

function unlock_analyze_button() {
    document.getElementById("block_var_button").disabled = false;
}

//lock/unlock input field
function checkbox(object) {
    let id = object.id.split('@')[0];
    let input_cb = document.getElementById(id + '@block_cb');
    let input_value = document.getElementById(id + '@block_value');
    input_value.disabled = !input_cb.checked;
    input_value.value = 0;

    unlock_analyze_button()

    let vars_size = parseInt(document.getElementById("vars_size").innerText);
    for (let i = 1; i <= vars_size; i++) {
        let id = document.getElementById("vars_table").rows[i].cells[0].innerText;
        if (document.getElementById(id + "@block_cb").checked) {
            document.getElementById("block_var_button").disabled = false;
        }
    }
}

// block var entity
function BlockVar() {
    this.id = 0;
    this.value = 0;
}

function get_block_vars() {
    let vars_size = parseInt(document.getElementById("vars_size").innerText);
    let vars_array = [];
    for (let i = 1; i <= vars_size; i++) {
        let id = document.getElementById("vars_table").rows[i].cells[0].innerText;
        if (document.getElementById(id + "@block_cb").checked) {
            let obj = new BlockVar()
            obj['id'] = parseInt(id);
            obj['value'] = parseInt(document.getElementById(id + "@block_value").value);
            vars_array.push(obj);
        }
    }
    return JSON.stringify(vars_array);
}

// creates an array of blocking values
function analyze() {
    show_analyze_load(true);
    document.getElementById("block_var_data").value = get_block_vars();
}

function more_steps() {
    let input = document.getElementById("input_steps");
    input.value = parseInt(input.value) + 1;
}

function less_steps() {
    let input = document.getElementById("input_steps");
    if (parseInt(input.value) > 1) {
        input.value = parseInt(input.value) - 1;
    }
}

function check_steps() {
    let input = document.getElementById("input_steps");
    if (parseInt(input.value) < 1) {
        input.value = 1;
    }
}

function simulate() {

    show_simulate_load(true)
    let block_vars = get_block_vars();
    let steps = document.getElementById("input_steps").value;

    let xhr = new XMLHttpRequest();
    xhr.open('GET', '/simulate', true);
    xhr.setRequestHeader("block_vars", block_vars);
    xhr.setRequestHeader("steps", steps);

    xhr.onload = function () {
        show_simulate_load(false)
        let data = JSON.parse(JSON.parse(xhr.response));

        let chart_el = document.getElementById('chart');
        let ctx = chart_el.getContext('2d');
        let chart = new Chart(document.getElementById("chart"), data);

    };

    xhr.send();
}

function add_test(name, test_text) {
    let table = document.getElementById("test_table");
    let row = table.insertRow(-1);

    let cell_name = row.insertCell(0);
    let cell_test = row.insertCell(1);
    let cell_result = row.insertCell(2);
    let cell_delete = row.insertCell(3);

    let btn_id = (table.rows.length - 1) + "@test_delete_btn";
    let test_name_id = (table.rows.length - 1) + "@test_name";
    let test_id = (table.rows.length - 1) + "@test_input";
    let test_res_id = (table.rows.length - 1) + "@test_result";

    if (name === undefined) {
        name = `Test ${table.rows.length - 1}`;
    }

    cell_name.innerHTML = `<div id="${test_name_id}" class="test_name" contenteditable='true'>${name}</div>`;
    cell_test.innerHTML = `<textarea id="${test_id}" class='test_input' onblur="update_test_table_height()">${test_text}</textarea>`;
    cell_result.innerHTML = `<div id="${test_res_id}" class="test_result"></div>`
    cell_delete.innerHTML = `<input id='${btn_id}' class='close_btn' type='image' onclick='delete_row(this)' src=\"https://img.icons8.com/cotton/2x/delete-sign--v1.png\"/>`

    update_test_table_height();
}

function delete_row(btn) {
    let id = parseInt(btn.id.split('@')[0]);
    let table = document.getElementById("test_table");

    table.deleteRow(id);
    update_test_table_indexes();

    tests_global = get_test_from_table();
}

function update_test_table_indexes() {
    let table = document.getElementById("test_table");

    for (let i = 1; i < table.rows.length; i++) {

        let curr_id = parseInt(table.rows[i].cells[0].innerHTML.match("\"\\w+@\\w+\"")[0].split('@')[0][1])

        document.getElementById(curr_id + "@test_name").id = i + "@test_name";
        document.getElementById(curr_id + "@test_input").id = i + "@test_input";
        document.getElementById(curr_id + "@test_result").id = i + "@test_result";
        document.getElementById(curr_id + "@test_delete_btn").id = i + "@test_delete_btn";
    }
}

function update_test_table_height() {
    let content = document.getElementById("testing_lab_result");
    content.style.maxHeight = content.scrollHeight + "px";
}

function TestStr() {
    this.id = 0;
    this.name = "";
    this.test = "";
}

function run_test() {

    show_test_load(true)
    clear_test_results();

    let str_tests = get_test_from_table();

    save_tests_to_cookie(str_tests);

    let xhr = new XMLHttpRequest();
    xhr.open('GET', '/test_all', true);
    xhr.setRequestHeader("tests", JSON.stringify(str_tests));

    console.log(JSON.stringify(str_tests))

    xhr.onload = function () {
        let tests_response = JSON.parse(JSON.parse(xhr.response));
        console.log(tests_response);

        for (let i = 0; i < tests_response.length; i++) {

            let test_response = tests_response[i];

            let table_result = document.getElementById(test_response.Id + "@test_result");

            if (test_response['SyntaxError'] !== undefined) {
                table_result.innerText = test_response['SyntaxError'];
                table_result.style.color = "#fc003f";
            } else {
                if (test_response['TestSuccess'] === true) {
                    table_result.innerText = "Success";
                    table_result.style.color = "#37cf19";
                } else {
                    table_result.innerText = "Failed";
                    table_result.style.color = "#fc003f";
                }
            }

        }

        show_test_load(false);

    }

    xhr.send();

    // for (let i = 0; i < str_tests.length; i++) {
    //     let test = str_tests[i];
    //
    //     let xhr = new XMLHttpRequest();
    //     xhr.open('GET', '/test', true);
    //     xhr.setRequestHeader("test", JSON.stringify(test));
    //
    //     xhr.onload = function () {
    //         let test_response = JSON.parse(JSON.parse(xhr.response));
    //
    //         console.log(test_response);
    //
    //         let table_result = document.getElementById(test_response.id + "@test_result");
    //
    //         if (test_response['syntaxError'] !== undefined) {
    //             table_result.innerText = test_response['syntaxError'];
    //             table_result.style.color = "#fc003f";
    //         } else {
    //             if (test_response['testSuccess'] === true) {
    //                 table_result.innerText = "Success";
    //                 table_result.style.color = "#37cf19";
    //             } else {
    //                 table_result.innerText = "Failed";
    //                 table_result.style.color = "#fc003f";
    //             }
    //         }
    //
    //         if (test_response.id === str_tests.length) {
    //             show_test_load(false);
    //         }
    //     }
    //
    //     xhr.send();
    // }
}

function get_test_from_table() {
    let table = document.getElementById("test_table");
    let str_tests = [];

    for (let i = 1; i < table.rows.length; i++) {
        let test_str = new TestStr();
        test_str.id = i;
        test_str.name = document.getElementById(i + "@test_name").innerHTML;
        test_str.test = document.getElementById(i + "@test_input").value;
        str_tests.push(test_str);
    }

    tests_global = str_tests;

    return str_tests;
}

function save_tests_to_cookie(tests) {
    let xhr = new XMLHttpRequest();
    xhr.open('GET', '/save_tests', true);
    xhr.setRequestHeader("new_tests", JSON.stringify(tests));
    xhr.send();

    tests_global = tests
}

function get_tests_from_cookie() {
    let xhr = new XMLHttpRequest();
    xhr.open('GET', '/get_saved_tests', true);

    xhr.onload = function () {
        let tests = JSON.parse(JSON.parse(xhr.response));
        clear_tests();

        let table = document.getElementById("test_table");
        for (let i = 0; i < tests.length; i++) {
            add_test();
            let test = tests[i];
            document.getElementById((i + 1) + "@test_name").innerHTML = test["name"];
            document.getElementById((i + 1) + "@test_input").innerHTML = test["test"];
        }

        update_test_table_height();
        close_test_lab(true);

        tests_global = tests;
    }

    xhr.send();
}

function clear_tests() {
    let table = document.getElementById("test_table");
    for (let i = 1; i < table.rows.length; i++) {
        table.deleteRow(i);
    }
    tests_global = [];
}

function clear_test_results() {
    let table = document.getElementById("test_table");

    for (let i = 1; i < table.rows.length; i++) {
        let res = document.getElementById(i + "@test_result");
        res.innerText = "";
        res.style.color = "black";
    }
}

function close_test_lab(cond) {
    let btn = document.getElementById("btn_test_lab");
    open_collapsable(true, btn);
}

function close_simulation(cond) {
    let btn = document.getElementById("btn_simulate_collapse");
    open_collapsable(true, btn);
}

function open_collapsable(cond, btn) {
    if (cond) {
        btn.classList.toggle("after");
    } else {
        btn.classList.toggle("active");
    }
    let content = btn.nextElementSibling;
    if (content.style.maxHeight) {
        content.style.maxHeight = null;
    } else {
        content.style.maxHeight = content.scrollHeight + "px";
    }
}


function show_analyze_load(cond) {
    show_load(cond, document.getElementById("load_analyze"));
}

function show_test_load(cond) {
    show_load(cond, document.getElementById("load_test"));
}

function show_simulate_load(cond) {
    show_load(cond, document.getElementById("load_simulate"));
}

function show_load(cond, load) {
    if (cond) {
        load.style.display = "inline-block";
    } else {
        load.style.display = "none";
    }
}

function upload_file() {
    let input = document.createElement('input');
    input.type = 'file';
    input.onchange = e => {
        let file = e.target.files[0];
        let reader = new FileReader();
        reader.readAsText(file, 'UTF-8');

        reader.onload = readerEvent => {
            let content = readerEvent.target.result; // this is the content!
            show_tests(JSON.parse(content));
        }

    }
    input.click();
}

function show_tests(tests) {
    console.log(tests);
    for (let i = 0; i < tests.length; i++) {
        add_test(tests[i].name, tests[i].test);
    }

    tests_global = tests;
}

function download_tests() {
    console.log(tests_global);

    let dataStr = "data:text/json;charset=utf-8," + encodeURIComponent(JSON.stringify(tests_global));
    let downloadAnchorNode = document.createElement('a');
    downloadAnchorNode.setAttribute("href", dataStr);
    downloadAnchorNode.setAttribute("download", "tests.json");
    document.body.appendChild(downloadAnchorNode); // required for firefox
    downloadAnchorNode.click();
    downloadAnchorNode.remove();
}

function new_model() {
    let input = document.createElement('input_new_model');
    input.type = 'file';
    input.onchange = e => {
        let file = e.target.files[0];
        let reader = new FileReader();
        reader.readAsText(file, 'UTF-8');

        reader.onload = readerEvent => {
            let content = readerEvent.target.result; // this is the content!

            let form = document.createElement("form_newmodel");
            let element1 = document.createElement("input_newmodel");

            form.method = "POST";
            form.action = "/";

            element1.value = content;
            element1.name = "json";
            form.appendChild(element1);
            document.body.appendChild(form);
            form.submit();
        }

    }
    input.click();
}