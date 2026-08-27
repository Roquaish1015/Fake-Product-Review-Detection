/* REVIEW//SENTINEL — Interactive Engine & Functional Handlers */

document.addEventListener('DOMContentLoaded', () => {
    initParticles();
    initNavbarScroll();
    initHeroMouseParallax();
    initStarRatingSelector();
    initStickyScrollSteps();
    initFormHandlers();
});

/* Floating Canvas Particle Engine */
function initParticles() {
    const canvas = document.getElementById('particles-canvas');
    if (!canvas) return;

    const ctx = canvas.getContext('2d');
    let width = canvas.width = window.innerWidth;
    let height = canvas.height = window.innerHeight;

    window.addEventListener('resize', () => {
        width = canvas.width = window.innerWidth;
        height = canvas.height = window.innerHeight;
    });

    const particles = [];
    const count = Math.min(45, Math.floor(width / 35));

    for (let i = 0; i < count; i++) {
        particles.push({
            x: Math.random() * width,
            y: Math.random() * height,
            radius: Math.random() * 1.5 + 0.5,
            vx: (Math.random() - 0.5) * 0.2,
            vy: (Math.random() - 0.5) * 0.2,
            alpha: Math.random() * 0.3 + 0.1
        });
    }

    function animate() {
        ctx.clearRect(0, 0, width, height);

        particles.forEach(p => {
            p.x += p.vx;
            p.y += p.vy;

            if (p.x < 0) p.x = width;
            if (p.x > width) p.x = 0;
            if (p.y < 0) p.y = height;
            if (p.y > height) p.y = 0;

            ctx.beginPath();
            ctx.arc(p.x, p.y, p.radius, 0, Math.PI * 2);
            ctx.fillStyle = `rgba(124, 58, 237, ${p.alpha})`;
            ctx.fill();
        });

        requestAnimationFrame(animate);
    }

    animate();
}

/* Navbar Scroll Handler */
function initNavbarScroll() {
    const nav = document.getElementById('navbar');
    if (!nav) return;

    window.addEventListener('scroll', () => {
        if (window.scrollY > 40) {
            nav.classList.add('scrolled');
        } else {
            nav.classList.remove('scrolled');
        }
    });
}

/* Hero Orbital Core Subtle Mouse Parallax */
function initHeroMouseParallax() {
    const field = document.getElementById('heroOrbitalField');
    if (!field) return;

    const ringOuter = document.getElementById('ringOuter');
    const ringInner = document.getElementById('ringInner');
    const nodeTop = document.getElementById('nodeTop');
    const nodeBottom = document.getElementById('nodeBottom');
    const nodeLeft = document.getElementById('nodeLeft');
    const nodeRight = document.getElementById('nodeRight');

    window.addEventListener('mousemove', (e) => {
        const x = (e.clientX / window.innerWidth - 0.5) * 20;
        const y = (e.clientY / window.innerHeight - 0.5) * 20;

        if (ringOuter) ringOuter.style.transform = `translate(${x * 0.8}px, ${y * 0.8}px)`;
        if (ringInner) ringInner.style.transform = `translate(${x * -0.5}px, ${y * -0.5}px)`;

        if (nodeTop) nodeTop.style.transform = `translate(calc(-50% + ${x * 0.4}px), ${y * 0.4}px)`;
        if (nodeBottom) nodeBottom.style.transform = `translate(calc(-50% + ${x * -0.4}px), ${y * -0.4}px)`;
        if (nodeLeft) nodeLeft.style.transform = `translate(${x * 0.5}px, calc(-50% + ${y * 0.5}px))`;
        if (nodeRight) nodeRight.style.transform = `translate(${x * -0.5}px, calc(-50% + ${y * -0.5}px))`;
    });
}

/* Interactive 5-Star Rating Selector (☆ ☆ ☆ ☆ ☆) */
let selectedStarRating = 5;

function initStarRatingSelector() {
    const container = document.getElementById('starRatingSelector');
    if (!container) return;

    const stars = container.querySelectorAll('.star-symbol');
    const hiddenVal = document.getElementById('heroRatingValue');

    stars.forEach((star, index) => {
        const value = index + 1;

        star.addEventListener('mouseenter', () => {
            highlightStars(stars, value);
        });

        star.addEventListener('mouseleave', () => {
            highlightStars(stars, selectedStarRating);
        });

        star.addEventListener('click', () => {
            selectedStarRating = value;
            if (hiddenVal) hiddenVal.value = value;
            highlightStars(stars, selectedStarRating);
        });
    });

    highlightStars(stars, selectedStarRating);
}

function highlightStars(stars, count) {
    stars.forEach((star, idx) => {
        if (idx < count) {
            star.textContent = '★';
            star.classList.add('active');
        } else {
            star.textContent = '☆';
            star.classList.remove('active');
        }
    });
}

/* Section 6 Sticky Scroll Step Activator */
function initStickyScrollSteps() {
    const steps = document.querySelectorAll('.scroll-step-item');
    const stickyTitle = document.getElementById('stickyStepTitle');

    if (!steps.length || !stickyTitle) return;

    const observer = new IntersectionObserver((entries) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                steps.forEach(s => s.classList.remove('active-step'));
                entry.target.classList.add('active-step');
                const code = entry.target.querySelector('.step-num-code');
                if (code) {
                    stickyTitle.textContent = code.textContent;
                }
            }
        });
    }, { threshold: 0.6 });

    steps.forEach(step => observer.observe(step));
}

/* Form Handlers (Text & URL Mode) */
function initFormHandlers() {
    // Text Mode Form Submit
    const heroTextForm = document.getElementById('heroAnalyzerForm');
    if (heroTextForm) {
        heroTextForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            const product = document.getElementById('heroProduct').value.trim();
            const title = document.getElementById('heroTitle') ? document.getElementById('heroTitle').value.trim() : 'Review';
            const content = document.getElementById('heroReview').value.trim();

            if (!product || !content) {
                alert('Please enter both Product Name and Review Content.');
                return;
            }

            const res = await fetch('/api/analyze', {
                method: 'POST',
                headers: {'Content-Type': 'application/json'},
                body: JSON.stringify({
                    productName: product,
                    rating: selectedStarRating,
                    reviewTitle: title || 'Review Analysis',
                    reviewContent: content,
                    verifiedPurchase: false
                })
            });

            const data = await res.json();
            renderAnalysisResult(data);
        });
    }

    // URL Mode Form Submit & Validation
    const heroUrlForm = document.getElementById('heroUrlForm');
    if (heroUrlForm) {
        heroUrlForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            const rawUrl = document.getElementById('heroProductUrl').value.trim();
            const notice = document.getElementById('urlValidationNotice');

            if (!rawUrl || (!rawUrl.startsWith('http://') && !rawUrl.startsWith('https://'))) {
                if (notice) {
                    notice.style.display = 'block';
                    notice.style.background = 'rgba(255, 59, 92, 0.15)';
                    notice.style.border = '1px solid var(--danger-red)';
                    notice.style.color = 'var(--danger-red)';
                    notice.textContent = 'INVALID URL: Please enter a valid http:// or https:// URL.';
                }
                return;
            }

            if (notice) {
                notice.style.display = 'block';
                notice.style.background = 'rgba(0, 229, 255, 0.15)';
                notice.style.border = '1px solid var(--accent-cyan)';
                notice.style.color = 'var(--accent-cyan)';
                notice.textContent = 'URL RECEIVED: Extracting product metadata and review signals...';
            }

            try {
                const res = await fetch('/api/analyze-url', {
                    method: 'POST',
                    headers: {'Content-Type': 'application/json'},
                    body: JSON.stringify({ url: rawUrl })
                });

                if (res.ok) {
                    const report = await res.json();
                    renderUrlReportOutput(report);
                } else {
                    if (notice) {
                        notice.style.background = 'rgba(255, 184, 0, 0.15)';
                        notice.style.border = '1px solid var(--warning-yellow)';
                        notice.style.color = 'var(--warning-yellow)';
                        notice.textContent = 'URL RECEIVED: Review content could not be retrieved automatically. You can paste the review text in the Write tab.';
                    }
                }
            } catch (err) {
                console.error(err);
            }
        });
    }
}

function renderAnalysisResult(data) {
    const waiting = document.getElementById('initialWaitingState');
    const revealed = document.getElementById('revealedResultState');
    if (waiting) waiting.style.display = 'none';
    if (revealed) revealed.style.display = 'block';

    const verdictEl = document.getElementById('revealedVerdict');
    const scoreEl = document.getElementById('revealedScore');

    if (verdictEl) verdictEl.textContent = data.verdict;
    if (scoreEl) scoreEl.textContent = Math.round(data.fakeProbability) + '%';

    const color = data.fakeProbability > 60 ? '#FF3B5C' : (data.fakeProbability > 35 ? '#FFB800' : '#00E676');
    if (verdictEl) verdictEl.style.color = color;
    if (scoreEl) scoreEl.style.color = color;
}

function renderUrlReportOutput(report) {
    const waiting = document.getElementById('initialWaitingState');
    const revealed = document.getElementById('revealedResultState');
    if (waiting) waiting.style.display = 'none';
    if (revealed) revealed.style.display = 'block';

    const verdictEl = document.getElementById('revealedVerdict');
    const scoreEl = document.getElementById('revealedScore');

    if (verdictEl) verdictEl.textContent = report.overallVerdict;
    if (scoreEl) scoreEl.textContent = Math.round(report.overallFakeProbability) + '%';

    const color = report.overallFakeProbability > 50 ? '#FF3B5C' : (report.overallFakeProbability > 35 ? '#FFB800' : '#00E676');
    if (verdictEl) verdictEl.style.color = color;
    if (scoreEl) scoreEl.style.color = color;
}
