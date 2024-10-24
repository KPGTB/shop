import {useEffect, useMemo} from "react"
import {GoXCircle} from "react-icons/go"
import {IoMdCheckmarkCircleOutline} from "react-icons/io"
import {useSearchParams} from "react-router-dom"

import {useCart} from "../../context/CartContext"
import styles from "./Payment.module.scss"

const PaymentPage = () => {
	const [_, setCart] = useCart()
	const [params] = useSearchParams()
	const success = useMemo(() => params.has("success"), [params])

	useEffect(() => {
		setCart([])
	}, [])

	return (
		<section className={styles.container}>
			<section className={styles.icon}>
				{success ? (
					<IoMdCheckmarkCircleOutline className={styles.success} />
				) : (
					<GoXCircle className={styles.fail} />
				)}
			</section>
			<h1>{success ? "Payment success" : "Payment failed"}</h1>
			<p>
				{success
					? "You will recevie confirmation e-mail soon"
					: "Something went wrong. Please try again later"}
			</p>
		</section>
	)
}

export default PaymentPage
