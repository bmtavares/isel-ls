import { div, a, h3, p, small,select,button ,option} from "./createElement.js";
import userUtils from "../user.js";
import bootstrapGenerator from "./bootstrapGenerator.js";
import { generateFormModal } from "./formGenerator.js";
import appConstants from "../appConstants.js";
import popup from "./popup.js";

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


function updatePositionOptions() {
 let lists = JSON.parse(localStorage.getItem("GlobalLists"))
 //listDropdown  = document.getElementById('listDropdown');
  const selectedList = listDropdown.value;
console.log(lists)
  // Clear previous options
  positionDropdown.innerHTML = '';
  let list = lists[selectedList]

 let PositionOptions = []
     for (let i = 0; i <= list.ncards; i++) {
       //PositionOptions += `<option value="${i}">Position ${i}</option>`;
       PositionOptions.push( option(`Position ${i}`,{value:`${i}`}))
     }
  console.log(PositionOptions)
  PositionOptions.forEach(object => {
    document.getElementById('positionDropdown').appendChild(object);
  });


}

function updateGlobalLis(boardId){

fetch(
    `${appConstants.API_BASE_URL}` + "boards/" + boardId + "/lists",
    {
        headers: userUtils.getAuthorizationHeader(),
    }
).then((res) => res.json())
.then((lists) =>
localStorage.setItem("GlobalLists", JSON.stringify(lists)))
console.log(localStorage.getItem("GlobalLists"))

}

function popfrommodal(listOptions,PositionOptions){
var body = div( {class:"modal-body"},
            p("move card ???"),
            select({id:"listDropdown"},...listOptions),
            select({id:"positionDropdown"},...PositionOptions)
            );
        popup.myGenerateModal(
        "MoveCardModal",
        "move card",
        body,
        button("Cancel",{type:"button", class:"btn btn-secondary", "data-bs-dismiss":"modal"}),
        button("Delete",{type:"button", class:"btn btn-primary", id:"confirmMoveButton"})
         )
}


function moveCard(card) {
 let lists = JSON.parse(localStorage.getItem("GlobalLists"))
 console.log(lists)
 let listOptions = []
  for (let i = 0; i < lists.length; i++) {
    //listOptions += `<option value="${i}">List ${i}</option>`;
    listOptions.push( option(`List ${i}`,{value:`${i}`}))
  }
  let PositionOptions = []
    for (let i = 0; i <= lists[0].ncards; i++) {
      //PositionOptions += `<option value="${i}">Position ${i}</option>`;
      PositionOptions.push( option(`Position ${i}`,{value:`${i}`}))
    }
    popfrommodal(listOptions,PositionOptions)

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

const listDropdown = document.getElementById('listDropdown');
listDropdown.addEventListener('change', updatePositionOptions);

  // Show the modal
  const modal = new bootstrap.Modal(document.getElementById('MoveCardModal'), {
    keyboard: false,
    backdrop: 'static'
  });
  modal.show();
}

function details(card) {
updateGlobalLis(card.boardId)

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
