import router from "./router.js";
import handlers from "./handlers.js";

window.addEventListener("load", loadHandler);
window.addEventListener("hashchange", hashChangeHandler);



function loadHandler() {
  router.addRouteHandler("home", handlers.getHome);
  router.addRouteHandler("userDetails", handlers.getUser);
  router.addRouteHandler("boards", handlers.getBoards);
  router.addRouteHandler("boards/{boardId}", handlers.getBoardDetail);
  router.addRouteHandler("board/{boardId}/user-list", handlers.getBoardsUsers);
  router.addRouteHandler(
    "boards/{boardId}/lists/{listId}",
    handlers.listDetails
  );
  router.addRouteHandler(
    "boards/{boardId}/lists/{listId}/cards/{cardId}",
    handlers.cardDetail
  );
  router.addDefaultNotFoundRouteHandler(() => (window.location.hash = "home"));
  hashChangeHandler();
}

function hashChangeHandler() {
  const mainContent = document.getElementById("mainContent");

  const path = window.location.hash.replace("#", "");
  const resp = router.getRouteHandler(path);
  resp.handler(mainContent, resp.params);
}
