import cardGenerator from "./cardGenerator.js";
import { div, a, h3, h1,h5,p,button } from "./createElement.js";
import userUtils from "../user.js";
import { generateFormModal } from "./formGenerator.js";
import bootstrapGenerator from "./bootstrapGenerator.js";
import appConstants from "../appConstants.js";
import popup from "./popup.js";

function listingCard(list) {
  return div(
      { class: 'card text-center' },
      div({ class: 'card-body' }, h3(list.name, { class: 'card-title' })),
      div(
          { class: 'card-footer' },
          div(
              { class: 'd-flex justify-content-between' },
              a('Details', {
                class: 'btn btn-primary',
                href: `#boards/${list.boardId}/lists/${list.id}`,
              }),
              a('Delete', {
                class: 'btn btn-danger',
                events: {
                  click: () => { listDelete(list); }
                }
              })
          )
      ));
}



function listDelete(list) {
const question = `Do you want to delete list: ${list.name} ?`;
        popup.myGenerateModal("deleteListModal","Delete List",p(question), button("Cancel",{type:"button", class:"btn btn-secondary", "data-bs-dismiss":"modal"}),
         button("Delete",{type:"button", class:"btn btn-primary", id:"confirmDeleteListButton"})
         )


  var confirmButton = document.getElementById('confirmDeleteListButton');

  var buttonClone = confirmButton.cloneNode(true);//cleans the event listener
  confirmButton.parentNode.replaceChild(buttonClone, confirmButton);
  confirmButton = document.getElementById('confirmDeleteListButton');

  confirmButton.addEventListener('click', () => {
    fetch(appConstants.API_BASE_URL + 'boards/' + list.boardId + '/lists/' + list.id, {
      method: 'DELETE',
      headers: userUtils.getAuthorizationHeader(),
    })
        .then(response => {
          console.log(response);
          if(response.ok){
            location.reload();
          }else{
            alert(`Delete list failed   error: ${response.statusText}`);
            location.reload();
          }
        })
        .catch(error => {
          console.log(error);
        });
  });
  // Show the modal
  const modall = new bootstrap.Modal(document.getElementById('deleteListModal'), {
    keyboard: false,
    backdrop: 'static'
  });
  modall.show();
}





function listing(lists) {
  return div(
      { class: "row gy-2" },
      ...lists.map((list) => div({ class: "col-lg-4" }, listingCard(list)))
  );
}

function details(boardId, list, cards, authHeader) {
  return div(
    cardGenerator.createFormModal(authHeader, boardId, list.id),
    h1(list.name),
    bootstrapGenerator.generateModalButton("Create new", "create-card-modal", "success"),
    cardGenerator.listing(cards)
    );
}

function createFormModal(authHeader, boardId) {
  const id = "create-list";

  async function handleOnSubmitCreate(e) {
    e.preventDefault();

    const formData = new FormData(e.srcElement); // Using the FormData object we can quickly fetch all the inputs
    const data = JSON.stringify(Object.fromEntries(formData.entries())); // Since we used names matching the desired keys in our input DTO, we can just send it directly

    //TODO: Wrap in try .. catch

    const creationReq = await fetch(`${appConstants.API_BASE_URL}boards/${boardId}/lists`, {
      method: "post",
      headers: { ...authHeader, "Content-Type": "application/json" },
      body: data,
    });

    //const result =
    await creationReq.json(); // Just await a response for now

    bootstrap.Modal.getInstance(document.querySelector(`#${id}-modal`)).hide(); // Hide the form modal

    window.dispatchEvent(new HashChangeEvent("hashchange")); // Assume everything went very much OK and do a soft refresh!

    // console.log(result.id);
  }

  return bootstrapGenerator.generateModal(
    id,
    generateFormModal(
      "list",
      handleOnSubmitCreate,
      [
        { type: "text", name: "Name", required: true }
      ],
      "Create"
    ),
    "Create a new list"
  );
}

export default {
  listing,
  details,
  createFormModal,
};