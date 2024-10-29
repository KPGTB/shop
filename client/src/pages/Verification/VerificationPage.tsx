import {useContext, useState} from "react"
import ReCAPTCHA from "react-google-recaptcha"
import {useNavigate, useSearchParams} from "react-router-dom"

import JsonForm from "../../components/Form/JsonForm"
import ThemeContext from "../../context/ThemeContext"
import {error} from "../../util/ToastUtl"
import styles from "./VerificationPage.module.scss"

const VerificationPage = ({
	title,
	endpoint,
	method,
	buttonText,
	successRedirect,
}: {
	title: string
	endpoint: string
	method: string
	buttonText: string
	successRedirect: string
}) => {
	const [params] = useSearchParams()
	const theme = useContext<Theme>(ThemeContext)
	const navigate = useNavigate()

	const [captcha, setCaptcha] = useState<string>("")

	return (
		<section className={styles.container}>
			<JsonForm
				method={method}
				action={endpoint}
				className={styles.form}
				reload={false}
				after={(res) =>
					res.json().then((json) => {
						if (json.status === 200) navigate(successRedirect)
						else {
							error(json.message, theme)
						}
					})
				}
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

				<h1>{title}</h1>

				<ReCAPTCHA
					onChange={(captchaToken) => setCaptcha(captchaToken || "")}
					sitekey={CAPTCHA_KEY}
				/>

				<button>{buttonText}</button>
			</JsonForm>
		</section>
	)
}

export default VerificationPage
