import { footer, ul, li, a, div, h1, p } from "./createElement.js";
import userUtils from "../user.js";
function creditsFooter() {
  return footer(
    {
      class: "fixed-bottom isel-bg-colour ms-1 mb-1 me-1 rounded",
    },
    ul(
      {
        class: "nav justify-content-center",
      },
      li(
        {
          class: "nav-item",
        },
        a("Made in Chelas by", {
          class: "nav-link text-body text-opacity-70",
        })
      ),
      li(
        {
          class: "nav-item",
        },
        a("Manuel Fonseca", {
          class: "nav-link link-light",
          href: "https://github.com/manuel-48052",
        })
      ),
      li(
        {
          class: "nav-item",
        },
        a("Sérgio Zorro", {
          class: "nav-link link-light",
          href: "https://github.com/sergiomiguelzorro",
        })
      ),
      li(
        {
          class: "nav-item",
        },
        a("Bruno Tavares", {
          class: "nav-link link-light",
          href: "https://github.com/bmtavares",
        })
      )
    )
  );
}

function content() {
    const user = userUtils.getUser()
    const msg = user
        ? `You are logged in as ${user.name}`
        : "Look's like you're not logged in.."
    return div(
        {
            class: "container-fluid text-center",
        },
        div(
            {
                class: "row justify-content-center",
            },
            h1("Welcome to LEIC Fauxllo!"),
            p(
                msg,
                {
                    class: "text-muted",
                }
            )
        )
    );
}

export default {
  content,
  creditsFooter,
};
