function filterProducts() {
    const searchInput = document.getElementById('searchInput');
    const filter = searchInput.value.toLowerCase();
    const products = document.getElementsByClassName('product-item');
    
    Array.from(products).forEach(product => {
        const productName = product.getAttribute('data-product-name').toLowerCase();
        const productCategory = product.getAttribute('data-product-category').toLowerCase();
        const productBrand = product.getAttribute('data-product-brand').toLowerCase();
        
        if (productName.includes(filter) || 
            productCategory.includes(filter) || 
            productBrand.includes(filter)) {
            product.style.display = '';
        } else {
            product.style.display = 'none';
        }
    });
}

document.addEventListener('DOMContentLoaded', function() {
    const searchInput = document.getElementById('searchInput');
    const sortSelect = document.getElementById('sortSelect');
    searchInput.addEventListener('input', updateProducts);
    sortSelect.addEventListener('change', updateProducts);

    function updateProducts() {
        const searchFilter = searchInput.value.toLowerCase();
        const sortValue = sortSelect.value;
        const productContainer = document.querySelector('.row.row-cols-1.row-cols-md-2.row-cols-lg-3.g-4');
        const products = Array.from(document.getElementsByClassName('product-item'));

        products.forEach(product => {
            const productName = product.getAttribute('data-product-name').toLowerCase();
            const productCategory = product.getAttribute('data-product-category').toLowerCase();
            const productBrand = product.getAttribute('data-product-brand').toLowerCase();
            
            if (productName.includes(searchFilter) || 
                productCategory.includes(searchFilter) || 
                productBrand.includes(searchFilter)) {
                product.style.display = '';
            } else {
                product.style.display = 'none';
            }
        });

        const visibleProducts = products.filter(p => p.style.display !== 'none');
        visibleProducts.sort((a, b) => {
            switch(sortValue) {
                case 'nameAsc':
                    return a.getAttribute('data-product-name')
                        .localeCompare(b.getAttribute('data-product-name'));
                case 'nameDesc':
                    return b.getAttribute('data-product-name')
                        .localeCompare(a.getAttribute('data-product-name'));
                case 'priceAsc':
                    return parseFloat(a.getAttribute('data-product-price')) - 
                           parseFloat(b.getAttribute('data-product-price'));
                case 'priceDesc':
                    return parseFloat(b.getAttribute('data-product-price')) - 
                           parseFloat(a.getAttribute('data-product-price'));
                default:
                    return 0;
            }
        });

        visibleProducts.forEach(product => {
            productContainer.appendChild(product);
        });
    }
});