import {Bounce, toast} from "react-toastify"

const error = (message: string, theme: Theme) => {
	toast.error(message, {
		position: "bottom-right",
		autoClose: 5000,
		hideProgressBar: false,
		closeOnClick: false,
		pauseOnHover: true,
		draggable: false,
		progress: undefined,
		theme: theme,
		transition: Bounce,
	})
}

const errorIf = (condition: boolean, message: string, theme: Theme) => {
	if (condition) error(message, theme)
}

const success = (message: string, theme: Theme) => {
	toast.success(message, {
		position: "bottom-right",
		autoClose: 5000,
		hideProgressBar: false,
		closeOnClick: false,
		pauseOnHover: true,
		draggable: false,
		progress: undefined,
		theme: theme,
		transition: Bounce,
	})
}

const successIf = (condition: boolean, message: string, theme: Theme) => {
	if (condition) success(message, theme)
}

export {error, errorIf, success, successIf}
