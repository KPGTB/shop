import {useContext, useEffect, useState} from "react"
import ReCAPTCHA from "react-google-recaptcha"
import {useSearchParams} from "react-router-dom"
import {Bounce, toast} from "react-toastify"

import ThemeContext from "../../context/ThemeContext"
import styles from "./ActivationPage.module.scss"

const ActivationPage = () => {
	const [params] = useSearchParams()
	const theme = useContext<Theme>(ThemeContext)

	const [captcha, setCaptcha] = useState<string>("")

	useEffect(() => {
		if (params.has("error")) {
			toast.error("Something went wrong!", {
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

		if (params.has("captcha")) {
			toast.error("Captcha failed!", {
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
	}, [])

	return (
		<section className={styles.container}>
			<form
				method="POST"
				action={`${API_URL}/auth/activate`}
				className={styles.form}
			>
				<input
					type="hidden"
					name="token"
					value={params.get("token") || ""}
				/>
				<input
					type="hidden"
					name="captcha"
					value={captcha}
				/>

				<h1>Account activation</h1>

				<ReCAPTCHA
					onChange={(captchaToken) => setCaptcha(captchaToken || "")}
					sitekey={CAPTCHA_KEY}
				/>

				<button>Activate account</button>
			</form>
		</section>
	)
}

export default ActivationPage
