let lastEvent = Date.now();

function update() {
    lastEvent = Date.now();
}

window.addEventListener('mousemove',update);
window.addEventListener('update',update);
window.addEventListener('scroll',update);


setInterval(() => {
    if (Date.now() - lastEvent < 30000) {
        fetch('/student/setActiveTime', {
            method: "POST",
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ lastEvent })
        });
    }
}, 120000);
