import styles from "./OrderImage.module.scss"

const OrderImage = ({images}: {images: string[]}) => {
	return (
		<section
			className={styles.container}
			style={{
				gridTemplateColumns: `repeat(${
					images.length > 1 ? 2 : 1
				} ,1fr)`,
				gridTemplateRows: `repeat(${images.length > 2 ? 2 : 1},1fr)`,
			}}
		>
			{[...Array(Math.min(4, images.length))].map((_, idx) => (
				<img
					key={idx}
					src={images[idx]}
					alt="PImg"
				/>
			))}
		</section>
	)
}

export default OrderImage
