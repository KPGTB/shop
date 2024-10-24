const businessProductsLoader = async () => {
	const categoriesRes = await fetch(`${API_URL}/category/list/full`)
	if (categoriesRes.status !== 200) return []
	const categoriesJson = await categoriesRes.json()

	const taxes: TaxCode[] = []
	let hasMore = true
	let page = 1

	while (hasMore) {
		const res = await fetch(`${API_URL}/product/taxes?page=${page}`)
		if (res.status !== 200) break
		const json = await res.json()
		hasMore = json.data.hasMore
		json.data.codes.forEach((code: TaxCode) => taxes.push(code))
		page++
	}

	return [categoriesJson.data as FullCategory[], taxes]
}

export default businessProductsLoader
