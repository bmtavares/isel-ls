// const TAG_TYPES = ["nav", "h1", "h2", "h3", "h4", "h5", "h6", "hr", "p", "b", "i", "u", "s", "a", "li", "ol", "ul", "img", "table", "thead", "tr", "td", "div", "span", "form", "button", "input", "label", "option", "select"]

/*
    USAGE:

    tag(                        // Use one of the available tag types for the function name
        "text",                 // If the tag has any contained text it should be the first argument
        {
            class: "primary",   // If the tag has any attributes they should be given via an argument before other elements
            type: "button",
            id: "cool-button"
        },
        othertag(...),          // Any elements children of this should come after text and options
        ...
    )
*/

function createElement(tagType, args) {
    const el = document.createElement(tagType)

    if (args.length) {
        // ``arguments`` is array-like. To use splice or forEach, needs to be converted
        // https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Functions/arguments#arguments_is_an_array-like_object
        const argsArr = [...args]

        // Check if there is text to append
        if (typeof argsArr[0] === "string") {
            el.appendChild(document.createTextNode(argsArr[0]))
            // Remove
            argsArr.splice(0, 1)
        }

        // If arguments still have anything, it *could* be an object that isn't an HTML Element
        // This is where the tag attributes can be found
        if (argsArr.length && (typeof argsArr[0] === "object" && !(argsArr[0] instanceof Element))) {
            // If the class property exists, add everything using classList
            el.className = argsArr[0].class ?? ""
            delete argsArr[0].class
            // Add all the remaining attributes
            Object.keys(argsArr[0]).forEach(k => el.setAttribute(k, args[0][k]))
            // Remove
            argsArr.splice(0, 1)
        }

        // All that remains should be HTML Elements
        argsArr.forEach(child => {
            el.appendChild(child)
        })
    }

    return el
}

export function nav() { return createElement("h1", arguments) }

export function h1() { return createElement("h1", arguments) }

export function h2() { return createElement("h2", arguments) }

export function h3() { return createElement("h3", arguments) }

export function hr() { return createElement("hr", arguments) }

export function p() { return createElement("p", arguments) }

export function b() { return createElement("b", arguments) }

export function i() { return createElement("i", arguments) }

export function u() { return createElement("u", arguments) }

export function s() { return createElement("s", arguments) }

export function a() { return createElement("a", arguments) }

export function li() { return createElement("li", arguments) }

export function ol() { return createElement("ol", arguments) }

export function ul() { return createElement("ul", arguments) }

export function img() { return createElement("img", arguments) }

export function table() { return createElement("table", arguments) }

export function thead() { return createElement("thead", arguments) }

export function tr() { return createElement("tr", arguments) }

export function td() { return createElement("td", arguments) }

export function div() { return createElement("div", arguments) }

export function span() { return createElement("span", arguments) }

export function form() { return createElement("form", arguments) }

export function button() { return createElement("button", arguments) }

export function input() { return createElement("input", arguments) }

export function label() { return createElement("label", arguments) }

export function option() { return createElement("option", arguments) }

export function select() { return createElement("select", arguments) }
