import {redirect} from "react-router-dom"

const productLoader = async ({
	params,
}: {
	params: {
		category?: string
		product?: string
	}
}) => {
	const productSplit = params.product?.split(".") || "-1"
	const productId = productSplit[productSplit.length - 1]

	const productRes = await fetch(API_URL + "/product/" + productId)
	if (productRes.status !== 200) return []
	const product = await productRes.json()
	let productData = product.data as ProductDto

	let fixedUrl = `${productData.category?.nameInUrl}.${productData.categoryId}/${productData.nameInUrl}.${productId}`
	if (`${params.category}/${params.product}` !== fixedUrl) {
		return redirect(`/shop/${fixedUrl}`)
	}

	return productData
}

export default productLoader
