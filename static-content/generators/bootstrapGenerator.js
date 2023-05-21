import { div, h3, button } from "./createElement.js";

function generateModal(id, body, title, footer) {
  return div(
    {
      class: "modal fade",
      tabindex: "-1",
      id: `${id}-modal`,
      "aria-hidden": "true",
    },
    div(
      { class: "modal-dialog" },
      /** Header **/
      div(
        { class: "modal-content" },
        title &&
          div({ class: "modal-header" }, h3(title, { class: "modal-title" })),
        /** Body **/
        div({ class: "modal-body" }, body),
        /** Footer **/
        footer ? div({ class: "modal-footer" }, footer) : null
      )
    )
  );
}

function generateModalButton(text, target, type = "primary") {
  return button(text, {
    type: "button",
    class: `btn btn-${type}`,
    "data-bs-toggle": "modal",
    "data-bs-target": `#${target}`,
  });
}

export default {
  generateModal,
  generateModalButton,
};
