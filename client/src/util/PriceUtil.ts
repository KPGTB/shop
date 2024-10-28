const toPrice = (price: number) => {
	return (Math.floor(price * 100.0) / 100.0).toFixed(2)
}

export {toPrice}
