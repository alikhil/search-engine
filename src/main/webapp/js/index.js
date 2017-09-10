$(document).ready(function () {

    console.log("scripts are running");

    var myHilitor = new Hilitor("result");
    myHilitor.setMatchType("left");

    function warn(message, color) {
        color = color | "danger";
        $("#dialogs")
            .append("<div class=\"alert alert-" + color + "\" id=\"result\">" + message + "</div>")
    }

    $("#buildIndexBtn").click(function() {
        console.log("build btn clicked");
        $("#link").attr("disabled", true);
        waitingDialog.show("Please wait! It can take a while.", {dialogSize: 'sm', progressType: 'info'})
        $.ajax({
            url: "/buildIndex",
            method: "post",
            complete: function (data) {
                console.log("response arrived");
                console.log(data);
                waitingDialog.hide();
            },
            success: function (data) {
                $("#dialogs")
                    .html("")
                    .append("<div class=\"alert alert-info\" id=\"result\">" + data + "</div>");
                $("#link").attr("disabled", false);
            },
            error: function (jqXHR, status, thrown) {
                warn(thrown);
                console.log(status);
                console.log(thrown);
            }
        });
    });

    $("#link").click(function (event) {
        console.log("search btn clicked");
        var text = $("#query").val();
        $("#link").attr("disabled", true);
        if (text == "")
        {
            warn("Please enter search query", "warning");
            return;
        }
        $.ajax({
            method: "GET",
            url: "/search",
            data: { query : text },
            success: getSuccess,
            complete: function (data) {
                console.log("response arrived");
                console.log(data);
                $("#link").attr("disabled", false);

            },
            error: function (jqXHR, status, thrown) {
                $("#dialogs")
                   warn(thrown);
                console.log(status);
                console.log(thrown);
            },
            dataType: "text"
        });
        // TODO: add animation
    });

    function getSuccess(data, status) {
        if (data) {
            myHilitor.remove();
            $("#result").empty();
            console.log(data);
            var response = JSON.parse(data);
            if (response.status == "error") {
                warn(response.data);
                return;
            }
            var items = response.data;
            if (items.length === 0)
                printItem("Nothing found with query:" + $("#query").val())
            for (var i = 0; i < items.length; i++) {
                printItem(items[i]);
            }
            myHilitor.apply(response.words);
        }
        console.log("request /search success");
        console.log(data);
    }
    function printItem(item) {
        $('#result').append("<div class=\"alert alert-success\" id=\"result\">" + item + "</div>");
    }
});