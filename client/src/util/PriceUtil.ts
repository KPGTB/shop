const toPrice = (price?: number) => {
	if (price === undefined) return "-"
	return (Math.floor(price * 100.0) / 100.0).toFixed(2)
}

export {toPrice}
