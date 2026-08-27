/* REVIEW//SENTINEL - Cybersecurity Dashboard Chart.js Visualizations */

document.addEventListener('DOMContentLoaded', () => {
    initDashboardCharts();
});

async function initDashboardCharts() {
    const verdictCanvas = document.getElementById('verdictChart');
    if (!verdictCanvas) return;

    try {
        const response = await fetch('/api/dashboard/stats');
        const stats = await response.json();

        renderVerdictChart(stats.verdictDistribution);
        renderRiskChart(stats.riskDistribution);
        renderSentimentChart(stats.sentimentDistribution);
        renderTrendChart(stats.timeTrendLabels, stats.timeTrendData);
    } catch (e) {
        console.error('Failed to load dashboard metrics:', e);
    }
}

function renderVerdictChart(dataMap) {
    const ctx = document.getElementById('verdictChart').getContext('2d');
    new Chart(ctx, {
        type: 'doughnut',
        data: {
            labels: Object.keys(dataMap),
            datasets: [{
                data: Object.values(dataMap),
                backgroundColor: ['#00E676', '#FFB800', '#FF3B5C'],
                borderColor: '#080B12',
                borderWidth: 3,
                hoverOffset: 8
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    position: 'bottom',
                    labels: { color: '#94A3B8', font: { family: 'JetBrains Mono' } }
                }
            },
            cutout: '70%'
        }
    });
}

function renderRiskChart(dataMap) {
    const ctx = document.getElementById('riskChart').getContext('2d');
    new Chart(ctx, {
        type: 'bar',
        data: {
            labels: Object.keys(dataMap),
            datasets: [{
                label: 'Risk Volume',
                data: Object.values(dataMap),
                backgroundColor: ['rgba(255, 59, 92, 0.7)', 'rgba(255, 184, 0, 0.7)', 'rgba(0, 230, 118, 0.7)'],
                borderColor: ['#FF3B5C', '#FFB800', '#00E676'],
                borderWidth: 1,
                borderRadius: 8
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: { display: false }
            },
            scales: {
                x: { ticks: { color: '#94A3B8' }, grid: { color: 'rgba(255, 255, 255, 0.05)' } },
                y: { ticks: { color: '#94A3B8' }, grid: { color: 'rgba(255, 255, 255, 0.05)' } }
            }
        }
    });
}

function renderSentimentChart(dataMap) {
    const ctx = document.getElementById('sentimentChart').getContext('2d');
    new Chart(ctx, {
        type: 'bar',
        data: {
            labels: Object.keys(dataMap),
            datasets: [{
                label: 'Reviews',
                data: Object.values(dataMap),
                backgroundColor: 'rgba(124, 58, 237, 0.65)',
                borderColor: '#7C3AED',
                borderWidth: 1,
                borderRadius: 6
            }]
        },
        options: {
            indexAxis: 'y',
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: { display: false }
            },
            scales: {
                x: { ticks: { color: '#94A3B8' }, grid: { color: 'rgba(255, 255, 255, 0.05)' } },
                y: { ticks: { color: '#94A3B8' }, grid: { color: 'rgba(255, 255, 255, 0.05)' } }
            }
        }
    });
}

function renderTrendChart(labels, dataPoints) {
    const ctx = document.getElementById('trendChart').getContext('2d');
    new Chart(ctx, {
        type: 'line',
        data: {
            labels: labels,
            datasets: [{
                label: 'Analyzed Reviews',
                data: dataPoints,
                borderColor: '#00E5FF',
                backgroundColor: 'rgba(0, 229, 255, 0.1)',
                fill: true,
                tension: 0.4,
                pointBackgroundColor: '#00E5FF',
                pointRadius: 5
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: { display: false }
            },
            scales: {
                x: { ticks: { color: '#94A3B8' }, grid: { color: 'rgba(255, 255, 255, 0.05)' } },
                y: { ticks: { color: '#94A3B8' }, grid: { color: 'rgba(255, 255, 255, 0.05)' } }
            }
        }
    });
}
