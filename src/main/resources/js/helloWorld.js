function post(url, data, callback) {

    var xhr = new XMLHttpRequest();
    xhr.open('POST', url);
    xhr.setRequestHeader('Content-Type', 'application/json');
    xhr.onload = function () {

        if (xhr.status === 200) {
            try {
                var json = JSON.parse(xhr.responseText);
                callback(data, json);
            } catch (e) {
                handleClientError(e, url, json);
            }

        } else {

            handleError(xhr);
        }
    };
    xhr.send(JSON.stringify(data));
// xhr.send(JSON.stringify({
//     name: 'John Smith',
//     age: 34
// }));
}
function get(url, callback) {
    var xhr = new XMLHttpRequest();
    xhr.open('GET', url);
    xhr.onload = function () {
        if (xhr.status === 200) {
            var json = JSON.parse(xhr.responseText);
            callback(json);
        }
        else {
            handleError(xhr);

        }
    };
    xhr.send();
}
function helloWorldPost() {
    post("/api/v1/HelloWorld", "testing", function (input, response) {
        console.log(response);
        alert(response.name + " " + response.age);
    });
}
function helloWorldGet() {
    get("/api/v1/HelloWorld?name=tester&age=90", function (response) {
        console.log(response);
        alert(response.name + " " + response.age);
    });
}
