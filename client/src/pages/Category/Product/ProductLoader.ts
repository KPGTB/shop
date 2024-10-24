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

	const productRes = await fetch(API_URL + "/product?id=" + productId)
	if (productRes.status !== 200) return []
	const product = await productRes.json()
	let productData = product.data as Product

	const categorySplit = params.category?.split(".") || "-1"
	const categoryId = categorySplit[categorySplit.length - 1]

	const categoryRes = await fetch(API_URL + "/category?id=" + categoryId)
	if (categoryRes.status !== 200) return []
	const category = await categoryRes.json()
	let categoryData = category.data as FullCategory

	let fixedUrl = `${categoryData.nameInUrl}.${categoryId}/${productData.nameInUrl}.${productId}`

	if (`${params.category}/${params.product}` !== fixedUrl) {
		return redirect(`/shop/${fixedUrl}`)
	}

	return [productData, categoryData]
}

export default productLoader
