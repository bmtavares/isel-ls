import { form, div, label, input, button } from "./createElement.js";

function generateField(domain, options) {
  const name = `${domain}-${options.name.toLowerCase().split(" ").join("-")}`;
  const inputOptions = { ...options };
  inputOptions.id = name;
  inputOptions.name = inputOptions.label ? inputOptions.name : inputOptions.name.toLowerCase();
  inputOptions.class = "form-control";
  delete inputOptions.label;
  return div(
    { class: "mb-3" },
    label(options.label ? options.label : options.name, { for: name, class: "col-form-label" }),
    input(inputOptions)
  );
}

export function generateFormModal(
  domain,
  onSubmit,
  fields,
  submitButtonText = "Submit"
) {
  return form(
    { id: `${domain}-form`, events: { submit: onSubmit } },
    ...fields.map((f) => generateField(domain, f)),
    div(
      { class: "modal-footer" },
      button("Cancel", {
        type: "button",
        class: "btn btn-secondary",
        "data-bs-dismiss": "modal",
      }),
      button(submitButtonText, { type: "submit", class: "btn btn-primary" })
    )
  );
}
