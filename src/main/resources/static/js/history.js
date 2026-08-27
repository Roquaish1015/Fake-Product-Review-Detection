/* REVIEW//SENTINEL - History Page Controls & Deletion Handler */

document.addEventListener('DOMContentLoaded', () => {
    initHistoryControls();
});

function initHistoryControls() {
    // Delete Button Listeners
    const deleteBtns = document.querySelectorAll('.btn-delete-review');
    deleteBtns.forEach(btn => {
        btn.addEventListener('click', async (e) => {
            e.preventDefault();
            const reviewId = btn.getAttribute('data-id');
            if (!reviewId) return;

            if (confirm('Are you sure you want to delete this review log from Sentinel database?')) {
                try {
                    const response = await fetch(`/api/reviews/${reviewId}`, {
                        method: 'DELETE'
                    });
                    const resData = await response.json();
                    if (resData.success) {
                        const card = btn.closest('.history-card');
                        if (card) {
                            card.style.transform = 'scale(0.8)';
                            card.style.opacity = '0';
                            setTimeout(() => card.remove(), 300);
                        }
                    } else {
                        alert('Failed to delete review.');
                    }
                } catch (err) {
                    console.error(err);
                    alert('Error deleting review.');
                }
            }
        });
    });

    // Search Input Enter key trigger
    const searchInput = document.getElementById('historySearchInput');
    if (searchInput) {
        searchInput.addEventListener('keypress', (e) => {
            if (e.key === 'Enter') {
                e.preventDefault();
                applyHistoryFilter();
            }
        });
    }
}

function filterByVerdict(verdict) {
    const url = new URL(window.location.href);
    url.searchParams.set('filter', verdict);
    window.location.href = url.toString();
}

function applyHistoryFilter() {
    const searchInput = document.getElementById('historySearchInput');
    const query = searchInput ? searchInput.value.trim() : '';
    const url = new URL(window.location.href);
    if (query) {
        url.searchParams.set('search', query);
    } else {
        url.searchParams.delete('search');
    }
    window.location.href = url.toString();
}
