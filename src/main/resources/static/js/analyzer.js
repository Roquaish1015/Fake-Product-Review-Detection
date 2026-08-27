/* REVIEW//SENTINEL - Analyzer Page & E-Commerce Link Scanner Engine */

document.addEventListener('DOMContentLoaded', () => {
    initRatingStars();
    initAnalyzerForm();
    initUrlForm();
});

let currentRating = 5;

function switchAnalyzerMode(mode) {
    const textPanel = document.getElementById('textModePanel');
    const urlPanel = document.getElementById('urlModePanel');
    const tabText = document.getElementById('tabTextMode');
    const tabUrl = document.getElementById('tabUrlMode');
    const urlReport = document.getElementById('urlReportSection');

    if (mode === 'url') {
        textPanel.style.display = 'none';
        urlPanel.style.display = 'block';
        tabText.classList.remove('active');
        tabUrl.classList.add('active');
    } else {
        textPanel.style.display = 'block';
        urlPanel.style.display = 'none';
        tabText.classList.add('active');
        tabUrl.classList.remove('active');
        if (urlReport) urlReport.style.display = 'none';
    }
}

function setPresetUrl(type) {
    const input = document.getElementById('productUrl');
    if (!input) return;

    if (type === 'flipkart') {
        input.value = 'https://www.flipkart.com/sony-wh-1000xm5-bluetooth-headset/p/itm123456789';
    } else if (type === 'amazon') {
        input.value = 'https://www.amazon.in/dp/B09X7C3AED/ref=nosim?tag=electronics-headphones';
    } else if (type === 'myntra') {
        input.value = 'https://www.myntra.com/headphones/boAt/rockerz-450-bluetooth-headset/12345/buy';
    }
}

function initRatingStars() {
    const starBtns = document.querySelectorAll('.star-btn');
    if (!starBtns.length) return;

    starBtns.forEach(btn => {
        btn.addEventListener('click', (e) => {
            e.preventDefault();
            const value = parseInt(btn.getAttribute('data-value'));
            currentRating = value;
            updateStarDisplay(value);
        });
    });
}

function updateStarDisplay(rating) {
    const starBtns = document.querySelectorAll('.star-btn');
    starBtns.forEach(btn => {
        const val = parseInt(btn.getAttribute('data-value'));
        if (val <= rating) {
            btn.classList.add('selected');
            btn.textContent = '★';
        } else {
            btn.classList.remove('selected');
            btn.textContent = '☆';
        }
    });
    const hiddenRatingInput = document.getElementById('ratingInput');
    if (hiddenRatingInput) {
        hiddenRatingInput.value = rating;
    }
}

function initAnalyzerForm() {
    const form = document.getElementById('analyzerForm');
    if (!form) return;

    form.addEventListener('submit', async (e) => {
        e.preventDefault();

        const productName = document.getElementById('productName').value.trim();
        const reviewTitle = document.getElementById('reviewTitle').value.trim();
        const reviewContent = document.getElementById('reviewContent').value.trim();
        const verifiedPurchase = document.getElementById('verifiedPurchase').checked;

        if (!productName || !reviewContent) {
            alert('Please provide both a Product Name and Review Text.');
            return;
        }

        const payload = {
            productName: productName,
            rating: currentRating,
            reviewTitle: reviewTitle,
            reviewContent: reviewContent,
            verifiedPurchase: verifiedPurchase
        };

        triggerScanningSequence('/api/analyze', payload, (resultData) => {
            if (resultData && resultData.reviewId) {
                window.location.href = `/result/${resultData.reviewId}`;
            }
        });
    });
}

function initUrlForm() {
    const urlForm = document.getElementById('urlForm');
    if (!urlForm) return;

    urlForm.addEventListener('submit', async (e) => {
        e.preventDefault();

        const urlInput = document.getElementById('productUrl').value.trim();
        if (!urlInput) {
            alert('Please enter a valid product URL.');
            return;
        }

        const urlSteps = [
            { text: 'INITIALIZING SENTINEL URL PARSER...', percent: 15 },
            { text: 'FETCHING E-COMMERCE PAGE METADATA...', percent: 32 },
            { text: 'EXTRACTING CUSTOMER REVIEWS...', percent: 54 },
            { text: 'SCANNING BOT PATTERNS & HYPERBOLE...', percent: 76 },
            { text: 'CALCULATING PRODUCT AUTHENTICITY SCORE...', percent: 92 },
            { text: 'GENERATING E-COMMERCE REPORT...', percent: 100 }
        ];

        triggerScanningSequence('/api/analyze-url', { url: urlInput }, (reportData) => {
            renderUrlReport(reportData);
        }, urlSteps);
    });
}

async function triggerScanningSequence(endpoint, payload, onSuccess, customSteps) {
    const overlay = document.getElementById('scanOverlay');
    const statusText = document.getElementById('scanStatusText');
    const progressFill = document.getElementById('scanProgressFill');
    const percentText = document.getElementById('scanPercentText');
    const circleProgress = document.querySelector('.scan-circle-progress');

    if (!overlay) return;

    overlay.classList.add('active');

    const steps = customSteps || [
        { text: 'INITIALIZING REVIEW ENGINE...', percent: 12 },
        { text: 'READING REVIEW...', percent: 28 },
        { text: 'ANALYZING SENTIMENT...', percent: 45 },
        { text: 'SCANNING LANGUAGE PATTERNS...', percent: 62 },
        { text: 'CHECKING REPETITION...', percent: 78 },
        { text: 'CALCULATING RISK...', percent: 91 },
        { text: 'GENERATING VERDICT...', percent: 100 }
    ];

    const apiPromise = fetch(endpoint, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
    }).then(res => res.json());

    for (let i = 0; i < steps.length; i++) {
        const step = steps[i];
        statusText.textContent = step.text;
        percentText.textContent = `${step.percent}%`;
        progressFill.style.width = `${step.percent}%`;

        if (circleProgress) {
            const strokeDashoffset = 502 - (502 * (step.percent / 100));
            circleProgress.style.strokeDashoffset = strokeDashoffset;
        }

        await new Promise(r => setTimeout(r, 600));
    }

    try {
        const data = await apiPromise;
        overlay.classList.remove('active');
        if (onSuccess) onSuccess(data);
    } catch (err) {
        console.error(err);
        alert('Analysis error. Please verify input and try again.');
        overlay.classList.remove('active');
    }
}

function renderUrlReport(report) {
    const reportSection = document.getElementById('urlReportSection');
    if (!reportSection) return;

    document.getElementById('urlVerdictIcon').textContent = report.overallVerdictIcon;
    document.getElementById('urlVerdictTitle').textContent = report.overallVerdict;

    const isFake = report.overallFakeProbability > 50;
    const isSusp = report.overallFakeProbability > 35;
    const color = isFake ? '#FF3B5C' : (isSusp ? '#FFB800' : '#00E676');

    document.getElementById('urlVerdictIcon').style.color = color;
    document.getElementById('urlVerdictTitle').style.color = color;

    document.getElementById('urlProductNameText').textContent = `Product: ${report.productName}`;
    document.getElementById('urlPlatformText').textContent = `Source: ${report.platform} • Scanned ${report.totalReviewsScanned} Reviews from Link`;

    document.getElementById('urlRiskScoreText').textContent = `${report.overallFakeProbability}%`;
    document.getElementById('urlRiskScoreText').style.color = color;

    document.getElementById('urlFakeCountText').textContent = report.fakeCount;
    document.getElementById('urlSuspiciousCountText').textContent = report.suspiciousCount;
    document.getElementById('urlGenuineCountText').textContent = report.genuineCount;

    const grid = document.getElementById('urlExtractedReviewsGrid');
    grid.innerHTML = '';

    report.analyzedReviews.forEach((rev, idx) => {
        const badgeClass = rev.verdict === 'LIKELY FAKE' ? 'badge-fake' : (rev.verdict === 'SUSPICIOUS' ? 'badge-suspicious' : 'badge-genuine');
        const cardHtml = `
            <div class="glass-card" style="padding: 1.25rem;">
                <div style="display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 0.75rem;">
                    <div>
                        <strong style="color: #FFF; font-size: 1.05rem;">Review #${idx + 1}: ${escapeHtml(rev.reviewTitle)}</strong>
                        <div style="color: #FFB800; font-size: 0.85rem; margin-top: 0.2rem;">${'★'.repeat(rev.rating) + '☆'.repeat(5 - rev.rating)}</div>
                    </div>
                    <span class="verdict-badge ${badgeClass}">${escapeHtml(rev.verdict)} (${Math.round(rev.fakeProbability)}%)</span>
                </div>
                <div style="color: var(--text-secondary); font-size: 0.9rem; line-height: 1.5; margin-bottom: 1rem;">
                    ${rev.highlightedHtml}
                </div>
                <div style="font-family: var(--font-mono); font-size: 0.75rem; color: var(--text-muted);">
                    Buyer Profile: ${rev.verifiedPurchase ? '✓ Verified Purchaser' : '⚠ Unverified Buyer'} • Risk: ${escapeHtml(rev.riskLevel)}
                </div>
            </div>
        `;
        grid.innerHTML += cardHtml;
    });

    reportSection.style.display = 'block';
    reportSection.scrollIntoView({ behavior: 'smooth' });
}

function escapeHtml(str) {
    if (!str) return '';
    return str.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;').replace(/"/g, '&quot;');
}
