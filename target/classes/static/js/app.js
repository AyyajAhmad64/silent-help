document.addEventListener("DOMContentLoaded", () => {
    const savedTheme = localStorage.getItem("silent-help-theme");
    if (savedTheme === "dark") {
        document.body.classList.add("dark-mode");
    }

    const themeToggle = document.getElementById("themeToggle");
    if (themeToggle) {
        themeToggle.addEventListener("click", () => {
            document.body.classList.toggle("dark-mode");
            localStorage.setItem("silent-help-theme", document.body.classList.contains("dark-mode") ? "dark" : "light");
        });
    }

    document.querySelectorAll(".alert-dismissible").forEach((alert) => {
        const delay = alert.classList.contains("alert-success") ? 12000 : 30000;
        window.setTimeout(() => {
            const instance = bootstrap.Alert.getOrCreateInstance(alert);
            instance.close();
        }, delay);
    });

    const title = document.querySelector("[name='title']");
    const tags = document.querySelector("[name='tags']");
    const suggestionBox = document.getElementById("smartSuggestions");
    const suggestionMap = [
        { keys: ["java", "spring", "code"], values: ["Attach error screenshot", "Mention Java/Spring version", "Ask for runnable example"] },
        { keys: ["dbms", "sql", "database"], values: ["Mention unit/topic", "Ask for ER diagram examples", "Add exam date"] },
        { keys: ["placement", "interview", "resume"], values: ["Add company/role", "Ask for roadmap", "Mention current skill level"] },
        { keys: ["hostel", "room", "mess"], values: ["Add block/floor", "Mention urgency", "Keep contact preference clear"] },
        { keys: ["notes", "pdf", "assignment"], values: ["Add subject code", "Mention required unit", "Attach existing material link"] }
    ];

    function renderSuggestions() {
        if (!suggestionBox) return;
        const value = `${title?.value || ""} ${tags?.value || ""}`.toLowerCase();
        const matches = suggestionMap.find((item) => item.keys.some((key) => value.includes(key)));
        const suggestions = matches ? matches.values : ["Use clear subject name", "Add deadline", "Mention preferred format"];
        suggestionBox.innerHTML = suggestions.map((item) => `<span>${item}</span>`).join("");
    }

    title?.addEventListener("input", renderSuggestions);
    tags?.addEventListener("input", renderSuggestions);
});
