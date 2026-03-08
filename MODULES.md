# VMS Intellect — Module Assignment & Preparation Guide

> **Presentation Date:** March 9, 2026  
> **Project:** Visitor Management System (VMS Intellect)  
> **Tech Stack:** Spring Boot (Backend) · Angular 16 (Frontend) · Tailwind CSS · JWT · Chart.js · ZXing QR Scanner  
> **Repository:** `/home/wanony/projects/backend-vms`

## 📐 Architecture Overview (For All Members)



---

## 📅 Presentation Order (Suggested)

| # | Member | Module | Duration |
|---|--------|--------|----------|
| 1 | Member 8 | Shared UI, Landing Page & Routing | 8 min |
| 2 | Member 1 | Authentication & Security | 8 min |
| 3 | Member 2 | User Management | 6 min |
| 4 | Member 7 | Core Entities & API Layer | 8 min |
| 5 | Member 5 | Associate Dashboard & Request Creation | 8 min |
| 6 | Member 4 | Admin Visit Request Management | 8 min |
| 7 | Member 3 | Admin Dashboard & Analytics | 8 min |
| 8 | Member 6 | Security Dashboard & QR Scanning | 8 min |
|   |         | **Q&A** | 10 min |
|   |         | **Total** | ~72 min |

> **Rationale:** This order follows the user journey — Landing → Login → User setup → Data layer → Associate creates request → Admin reviews → Dashboard analytics → Security check-in.

---

## 🛠️ Local Setup (For Demo Prep)

```bash
# Backend
cd /home/wanony/projects/backend-vms
./mvnw spring-boot:run

# Frontend
cd /home/wanony/projects/backend-vms/frontend
npm install
ng serve
# Open http://localhost:4200
```

## 📝 Preparation Checklist

- [ ] Read through your module code thoroughly
- [ ] Understand every line you'll explain (use the walkthrough above)
- [ ] Prepare for "what does this line do?" questions
- [ ] Run the application locally and test your module's flow
- [ ] Have screenshots/recordings as backup for live demo
- [ ] Prepare 2-3 "behind the scenes" insights (design decisions, trade-offs)
- [ ] Coordinate with adjacent module owners for integration demos
- [ ] Test all demos on the same machine before the presentation