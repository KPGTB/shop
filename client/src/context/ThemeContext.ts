import {createContext} from "react"

type Theme = "dark" | "light"
const ThemeContext = createContext<Theme>("light")
export default ThemeContext
export type {Theme}
