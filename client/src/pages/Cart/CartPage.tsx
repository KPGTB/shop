import {useMemo} from "react"
import {FaTrashCan, FaWallet} from "react-icons/fa6"
import {Link, useLoaderData} from "react-router-dom"

import {useCart} from "../../context/CartContext"
import styles from "./CartPage.module.scss"

const CartPage = () => {
	const [cartItems, setCartItems] = useCart()
	const products = useLoaderData() as Map<number, Product>

	const totalPrice = useMemo(() => {
		let price = 0
		cartItems.forEach(
			(item) =>
				(price += item.quantity * products.get(item.productId)!.price)
		)
		return price
	}, [cartItems])
	const currency = useMemo(() => {
		return cartItems.length === 0
			? ""
			: products.get(cartItems[0].productId)?.currency
	}, [cartItems])

	return (
		<section className={styles.container}>
			<article className={styles.items}>
				<h1>Items in cart</h1>
				{cartItems.map((item) => {
					const product = products.get(item.productId)

					if (product === undefined) return <></>

					return (
						<section
							key={product.id}
							className={styles.item}
						>
							<section className={styles.image}>
								<img
									src={product.image}
									alt="Product image"
								/>
							</section>

							<h3>
								<Link
									to={`/shop/${product.categoryId}/${product.id}`}
								>
									{product.name}
								</Link>
							</h3>
							<p>
								{item.quantity} x {product.price}{" "}
								{product.currency} ={" "}
								{item.quantity * product.price}{" "}
								{product.currency}
							</p>
							<button
								onClick={() => {
									setCartItems([
										...cartItems.filter(
											(i) => i.productId !== product.id
										),
									])
								}}
							>
								<FaTrashCan />
							</button>
						</section>
					)
				})}
			</article>

			<form
				action={`${API_URL}/payment`}
				method="POST"
				className={styles.checkout}
			>
				<h2>Checkout</h2>

				<p className={styles.info}>
					You will be able to choose payment method and use discount
					code on the next page
				</p>

				<hr />

				<p>
					{cartItems.map((item) => {
						const product = products.get(item.productId)

						if (product === undefined) return <></>

						return (
							<i key={`t_${item.productId}`}>
								<b>
									{product.name} x {item.quantity}
								</b>{" "}
								{item.quantity * product.price}{" "}
								{product.currency}
								<br />
							</i>
						)
					})}
					<br />
					<span className={styles.total}>
						<b>Total: </b> {totalPrice} {currency}
					</span>
				</p>

				<hr />

				{cartItems.map((item) => (
					<input
						type="hidden"
						name={`${item.productId}`}
						value={`${item.quantity}`}
						key={`p_${item.productId}`}
					/>
				))}
				<button disabled={cartItems.length === 0}>
					<FaWallet /> Pay using <i>Stripe</i>
				</button>
			</form>
		</section>
	)
}

export default CartPage
