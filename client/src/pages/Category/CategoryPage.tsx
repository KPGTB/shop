import {Link, useLoaderData} from "react-router-dom"

import {toPrice} from "../../util/PriceUtil"
import styles from "./CategoryPage.module.scss"

const CategoryPage = () => {
	const category = useLoaderData() as FullCategory

	return (
		<section className={styles.container}>
			<h1>{category.name}</h1>
			<p>{category.description}</p>

			<article className={styles.products}>
				{category.products.map((product) => (
					<ProductComp product={product} />
				))}
			</article>
		</section>
	)
}

const ProductComp = ({product}: {product: Product}) => {
	return (
		<Link
			to={`${product.nameInUrl}.${product.id}`}
			className={styles.product}
		>
			<img
				src={product.image}
				alt="Product image"
			/>
			<h3>{product.name}</h3>

			<p className={styles.desc}>{product.description}</p>

			<p>
				{toPrice(product.displayPrice)} {product.currency}
			</p>
		</Link>
	)
}

export default CategoryPage
