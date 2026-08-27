-- UJIANKU: tabel Bank Soal Guru
create extension if not exists pgcrypto;

create table if not exists public.questions (
    id uuid primary key default gen_random_uuid(),
    teacher_id uuid not null references auth.users(id) on delete cascade,
    question text not null,
    option_a text not null,
    option_b text not null,
    option_c text not null,
    option_d text not null,
    correct_answer text not null check (correct_answer in ('A','B','C','D')),
    created_at timestamptz not null default now()
);

alter table public.questions enable row level security;

drop policy if exists "teachers can read own questions" on public.questions;
create policy "teachers can read own questions"
    on public.questions for select
    to authenticated
    using (auth.uid() = teacher_id);

drop policy if exists "teachers can insert own questions" on public.questions;
create policy "teachers can insert own questions"
    on public.questions for insert
    to authenticated
    with check (auth.uid() = teacher_id);

drop policy if exists "teachers can update own questions" on public.questions;
create policy "teachers can update own questions"
    on public.questions for update
    to authenticated
    using (auth.uid() = teacher_id)
    with check (auth.uid() = teacher_id);

drop policy if exists "teachers can delete own questions" on public.questions;
create policy "teachers can delete own questions"
    on public.questions for delete
    to authenticated
    using (auth.uid() = teacher_id);
