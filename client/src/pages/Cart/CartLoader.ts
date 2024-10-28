import {getCart} from "../../context/CartContext"

const cartLoader = async () => {
	const itemsData = getCart()

	const result: Map<number, Product> = new Map()

	for (let item of itemsData) {
		const res = await fetch(API_URL + "/product?id=" + item.productId)
		if (res.status !== 200) continue
		const json = await res.json()
		result.set(item.productId, json.data)
	}

	return result
}

export default cartLoader
