import { div, a, h3, p, small } from "./createElement.js";
import userUtils from "../user.js";
const API_BASE_URL = "http://localhost:9000/";
import bootstrapGenerator from "./bootstrapGenerator.js";
import { generateFormModal } from "./formGenerator.js";
import appConstants from "../appConstants.js";

function listingCard(card) {
  return div(
    { class: "card text-center" },
    div({ class: "card-body" }, h3(card.name, { class: "card-title" })),
    div(
      { class: "card-footer d-grid" },
      a("Details", {
        class: "btn btn-primary",
        href: `#boards/${card.boardId}/lists/${card.listId}/cards/${card.id}`,
      })
    )
  );
}

function listing(cards) {
  return div(
    { class: "row gy-2" },
    ...cards.map((card) => div({ class: "col-lg-3" }, listingCard(card)))
  );
}

function moveCard(card) {
 let lists = JSON.parse(localStorage.getItem("GlobalLists"))
 console.log(lists)
 let listOptions = ""
  for (let i = 0; i < lists.length; i++) {
    listOptions += `<option value="${i}">List ${i}</option>`;
  }
  let PositionOptions = ""
    for (let i = 0; i < 100; i++) {
      PositionOptions += `<option value="${i}">Position ${i}</option>`;
    }
  const question = "mmove this card?";
  const modalHtml = `
    <div class="modal fade" id="MoveCardModal" tabindex="-1" aria-labelledby="MoveCardModalLabel" aria-hidden="true">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title" id="MoveCardModalLabel">Delete List</h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
          </div>
          <div class="modal-body">
            <p>${question}</p>
            <select id="listDropdown">
                ${listOptions}
             </select>
             <select id="positionDropdown">
             ${PositionOptions}

              </select>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">No</button>
            <button type="button" class="btn btn-primary" id="confirmMoveButton">Yes</button>
          </div>
        </div>
      </div>
    </div>`;

  // Add the modal HTML to the page
  document.body.insertAdjacentHTML('beforeend', modalHtml);

  // Add event listener to the confirm button
  const confirmButton = document.getElementById('confirmMoveButton');
    confirmButton.addEventListener('click', () => {
        const listDropdown = document.getElementById('listDropdown');
        const positionDropdown = document.getElementById('positionDropdown');
        const lid = listDropdown.value;
        const cix = positionDropdown.value;
      fetch(API_BASE_URL + 'boards/' + card.boardId + '/cards/' + card.id+"/move", {
        method: 'PUT',
        headers: userUtils.getAuthorizationHeader(),
        body: JSON.stringify({
        "lid": lid, //get from drop down
        "cix": cix //get from drop down
        })
      })
      .then(response => {
        console.log(response);
        if(response.ok){
        location.reload();
        }else{
        alert(`Move   error: ${response.statusText}`);
        location.reload();
        }
      })
      .catch(error => {
        console.log(error);
      });
    });


  // Show the modal
  const modal = new bootstrap.Modal(document.getElementById('MoveCardModal'), {
    keyboard: false,
    backdrop: 'static'
  });
  modal.show();
}

function details(card) {
  return div(
    { class: "card text-center" },
    div(
      { class: "card-body" },
      h3(card.name, { class: "card-title" }),
      p(card.description, { class: "card-text" }),
      card.dueDate &&
        p(
          { class: "card-text" },
          small(`📅 ${new Date(card.dueDate).toLocaleString()}`, {
            class: "text-body-secondary",
          })
        ),
        a('Move', {
                class: 'btn btn-primary',
                events: {
                  click: () => { moveCard(card); }
                }
              })
    )
  );
}

function createFormModal(authHeader, boardId, listId) {
  const id = "create-card";

  async function handleOnSubmitCreate(e) {
    e.preventDefault();

    const formData = new FormData(e.srcElement); // Using the FormData object we can quickly fetch all the inputs

    const formEntries = Object.fromEntries(formData.entries());
    formEntries.dueDate = formEntries.dueDate ? new Date(`${formEntries.dueDate}+00:00`).getTime() : null; // Convert to Unix Timestamp as UTC

    const data = JSON.stringify(formEntries); // Since we used names matching the desired keys in our input DTO, we can just send it directly

    //TODO: Wrap in try .. catch


    console.log(data);

    const creationReq = await fetch(`${appConstants.API_BASE_URL}boards/${boardId}/lists/${listId}/cards`, {
      method: "post",
      headers: { ...authHeader, "Content-Type": "application/json" },
      body: data,
    });

    //const result =
    await creationReq.json(); // Just await a response for now

    bootstrap.Modal.getInstance(document.querySelector(`#${id}-modal`)).hide(); // Hide the form modal

    window.dispatchEvent(new HashChangeEvent("hashchange")); // Assume everything went very much OK and do a soft refresh!

    console.log(result.id);
  }

  return bootstrapGenerator.generateModal(
    id,
    generateFormModal(
      "card",
      handleOnSubmitCreate,
      [
        { type: "text", name: "Name", required: true },
        { type: "text", name: "Description", required: true },
        { type: "datetime-local", name: "dueDate", label: "Due date" }
      ],
      "Create"
    ),
    "Create a new card"
  );
}

export default {
  listing,
  details,
  createFormModal
};
