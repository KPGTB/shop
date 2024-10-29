import {useCallback, useContext, useMemo, useState} from "react"
import {FaCartPlus} from "react-icons/fa6"
import {Link, useLoaderData} from "react-router-dom"

import {useCart} from "../../../context/CartContext"
import ThemeContext from "../../../context/ThemeContext"
import {toPrice} from "../../../util/PriceUtil"
import {success} from "../../../util/ToastUtl"
import styles from "./ProductPage.module.scss"

const ProductPage = () => {
	const [cartItems, setCartItems] = useCart()
	const [quantity, setQuantity] = useState<number>(1)
	const theme = useContext<Theme>(ThemeContext)

	const product = useLoaderData() as ProductDto
	const category = useMemo(() => product.category!, [product])

	const addToCart = useCallback(() => {
		let foundInCart = false
		cartItems.forEach((cartItem) => {
			if (cartItem.productId === product.id) {
				foundInCart = true
				cartItem.quantity += quantity
			}
		})

		if (!foundInCart) {
			cartItems.push({
				productId: product.id,
				quantity: quantity,
			})
		}
		setCartItems([...cartItems])

		success(`Added ${product.name} to cart!`, theme)
	}, [quantity, cartItems])

	return (
		<section className={styles.container}>
			<nav>
				<Link to="/">Home</Link> /{" "}
				<Link to={`/shop/${category.nameInUrl}.${category.id}`}>
					{category.name}
				</Link>{" "}
				/ {product.name}
			</nav>

			<article className={styles.product}>
				<img
					src={product.image}
					alt="Product image"
				/>
				<section className={styles.productInfo}>
					<h1>{product.name}</h1>
					<section className={styles.infoContainer}>
						<section>
							<p>{product.description}</p>
							<a href="#product_description">Read more</a>
						</section>

						<section className={styles.cart}>
							<select
								className={styles.quantity}
								onChange={(el) =>
									setQuantity(Number(el.target.value))
								}
							>
								{[...Array(10)].map((_, idx) => {
									return <option>{idx + 1}</option>
								})}
							</select>

							<button onClick={addToCart}>
								<FaCartPlus /> Add to cart -{" "}
								{toPrice(product.displayPrice * quantity)}{" "}
								{product.currency}
							</button>
						</section>
					</section>
				</section>
			</article>

			<article className={styles.desc}>
				<h2 id="product_description">Description</h2>
				<p>{product.description}</p>
			</article>
		</section>
	)
}

export default ProductPage
