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

    const policyScroll = document.getElementById("policyScroll");
    const policyAgree = document.getElementById("policyAgree");
    const policyAcceptButton = document.getElementById("policyAcceptButton");
    if (policyScroll && policyAgree && policyAcceptButton) {
        const updatePolicyControls = () => {
            const hasScrolled = policyScroll.scrollTop + policyScroll.clientHeight >= policyScroll.scrollHeight - 8;
            policyAgree.disabled = !hasScrolled;
            policyAcceptButton.disabled = !hasScrolled || !policyAgree.checked;
        };
        policyScroll.addEventListener("scroll", updatePolicyControls);
        policyAgree.addEventListener("change", updatePolicyControls);
        updatePolicyControls();
    }

    document.querySelectorAll(".alert-dismissible").forEach((alert) => {
        const delay = alert.classList.contains("alert-success") ? 12000 : 30000;
        window.setTimeout(() => {
            const instance = bootstrap.Alert.getOrCreateInstance(alert);
            instance.close();
        }, delay);
    });

    function showInlineToast(type, message) {
        let zone = document.querySelector(".toast-zone");
        if (!zone) {
            zone = document.createElement("div");
            zone.className = "toast-zone container mt-3";
            document.querySelector(".app-navbar")?.insertAdjacentElement("afterend", zone);
        }
        const alert = document.createElement("div");
        alert.className = `alert alert-${type} alert-dismissible fade show`;
        alert.setAttribute("role", "alert");
        alert.innerHTML = `<span>${message}</span><button type="button" class="btn-close" data-bs-dismiss="alert"></button>`;
        zone.prepend(alert);
        window.setTimeout(() => bootstrap.Alert.getOrCreateInstance(alert).close(), type === "success" ? 12000 : 30000);
    }

    function escapeHtml(value) {
        return String(value)
            .replaceAll("&", "&amp;")
            .replaceAll("<", "&lt;")
            .replaceAll(">", "&gt;")
            .replaceAll('"', "&quot;")
            .replaceAll("'", "&#039;");
    }

    function appendResponse(response) {
        const thread = document.getElementById("responsesThread");
        if (!thread) return;
        const card = document.createElement("div");
        card.className = "response-card response-card-new";
        card.innerHTML = `
            <div class="card-meta">
                <span><i class="bi bi-person-circle"></i> <span>${escapeHtml(response.author)}</span></span>
                <span>${escapeHtml(response.createdAt)}</span>
            </div>
            <p>${escapeHtml(response.body)}</p>
            <div class="d-flex flex-wrap gap-2 align-items-center">
                <span class="status-dot"><i class="bi bi-check2"></i> Posted</span>
                <span class="text-muted small">Helpful (${Number(response.helpfulCount) || 0})</span>
            </div>
        `;
        thread.append(card);
        document.querySelector(".responses-empty")?.remove();
        const count = document.getElementById("responseCount");
        if (count) {
            count.textContent = String((Number.parseInt(count.textContent, 10) || 0) + 1);
        }
        card.scrollIntoView({ behavior: "smooth", block: "nearest" });
    }

    document.querySelectorAll(".response-form").forEach((form) => {
        const textarea = form.querySelector("textarea[name='message']");
        const submit = form.querySelector(".response-submit");
        const submitContent = submit?.innerHTML;

        form.addEventListener("submit", (event) => {
            const message = textarea?.value.trim() || "";
            if (message.length === 0) {
                event.preventDefault();
                showInlineToast("danger", "Please write a response before posting.");
                textarea?.focus();
                return;
            }
            if (message.length < 5) {
                event.preventDefault();
                showInlineToast("danger", "Response should be at least 5 characters.");
                textarea?.focus();
                return;
            }
            if (!window.fetch) {
                return;
            }
            event.preventDefault();
            if (submit) {
                submit.disabled = true;
                submit.innerHTML = `<span class="response-submit-status"><span class="spinner-border spinner-border-sm" aria-hidden="true"></span>${form.dataset.loadingText || "Sending..."}</span>`;
            }
            fetch(form.action, {
                method: "POST",
                body: new FormData(form),
                headers: {
                    "X-Requested-With": "XMLHttpRequest",
                    "Accept": "application/json"
                },
                credentials: "same-origin"
            })
                .then(async (response) => {
                    const data = await response.json().catch(() => ({}));
                    if (!response.ok || data.ok === false) {
                        throw new Error(data.message || "Response could not be posted.");
                    }
                    appendResponse(data.response);
                    form.reset();
                    showInlineToast("success", data.message || "Response posted.");
                })
                .catch((error) => {
                    showInlineToast("danger", error.message || "Response could not be posted.");
                })
                .finally(() => {
                    if (submit) {
                        submit.disabled = false;
                        submit.innerHTML = submitContent;
                    }
                });
        });

        window.addEventListener("pageshow", () => {
            if (submit) {
                submit.disabled = false;
                submit.innerHTML = submitContent;
            }
        });
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
