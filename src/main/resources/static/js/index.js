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

showChart();

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

        let ctx = document.getElementById('chart').getContext('2d');
        let chart = new Chart(document.getElementById("chart"), data);

    };

    xhr.send();

}

function showChart() {
    let ctx = document.getElementById('chart').getContext('2d');
    let chart = new Chart(document.getElementById("chart"), {});
}