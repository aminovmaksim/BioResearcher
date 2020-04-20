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
    type : "line"
});

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
    document.getElementById("loading_wheel").style.display = "inline-block";
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

    document.getElementById("loading_wheel").style.display = "inline-block";
    let block_vars = get_block_vars();
    let steps = document.getElementById("input_steps").value;

    let xhr = new XMLHttpRequest();
    xhr.open('GET', '/simulate', true);
    xhr.setRequestHeader("block_vars", block_vars);
    xhr.setRequestHeader("steps", steps);

    xhr.onload = function () {
        document.getElementById("loading_wheel").style.display = "none";
        let data = JSON.parse(JSON.parse(xhr.response));

        let chart_el = document.getElementById('chart');
        let ctx = chart_el.getContext('2d');
        let chart = new Chart(document.getElementById("chart"), data);

    };

    xhr.send();
}

function add_test() {
    let table = document.getElementById("test_table");
    let row = table.insertRow(-1);

    let cell_name = row.insertCell(0);
    let cell_test = row.insertCell(1);
    let cell_result = row.insertCell(2);
    let cell_delete = row.insertCell(3);

    let btn_id = (table.rows.length - 1) + "@test_delete_btn";
    let test_id = (table.rows.length - 1) + "@test_input";
    let test_res_id = (table.rows.length - 1) + "@test_result";

    cell_delete.innerHTML = get_close_btn(btn_id);
    cell_name.innerHTML = `<div contenteditable='true'>Test ${table.rows.length - 1}</div>`;
    cell_test.innerHTML = `<textarea id="${test_id}" class='test_input' onchange='update_test_table_height()'></textarea>`;
    cell_result.innerHTML = `<div id="${test_res_id}"></div>`

    update_test_table_height();
}

function delete_row(btn) {
    let id = parseInt(btn.id.split('@')[0]);
    let table = document.getElementById("test_table");

    table.deleteRow(id);

    for (let i = 1; i < table.rows.length; i ++) {
        let id = i + "@test_row";
        table.rows[i].cells[3].innerHTML = get_close_btn(id);
    }
}

function update_test_table_indexes() {

}

function update_test_table_height() {
    let content = document.getElementById("testing_lab_result");
    content.style.maxHeight = content.scrollHeight + "px";
}

function get_close_btn(id) {
    return `<input id='${id}' class='close_btn' type='image' onclick='delete_row(this)' src=\"https://img.icons8.com/cotton/2x/delete-sign--v1.png\"/>`
}