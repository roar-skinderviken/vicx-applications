import Hero from "@/components/Hero"
import Image from "next/image"

import nginxImage from "@/assets/images/nginx.png"
import tomcatImage from "@/assets/images/tom.png"
import nodeImage from "@/assets/images/node.png"
import raspberryImage from "@/assets/images/rasp.png"
import tuxImage from "@/assets/images/Tux.png"
import archImage from "@/assets/images/arch.png"
import mysqlImage from "@/assets/images/mysql.png"


export const metadata = {
    title: "Portfolio | VICX"
}

export default function PortfolioPage() {
    return (
        <main className="content">
            <Hero
                title="Welcome to My Portfolio!"
                lead="Explore my projects and experience below."
            />

            <div className="container mx-auto my-5">
                <h2 className="text-center text-3xl my-4">My Projects</h2>

                <div className="flex flex-col items-center">
                    <div className="flex flex-col space-y-4 px-2">

                        <div className="project">
                            <h2>Web Development</h2>
                            <ul>
                                <li>Passionate about crafting engaging web experiences using HTML, CSS, and
                                    JavaScript.
                                </li>
                                <li>Using GitHub repositories to push changes and sync with my server.</li>
                                <li>Specializing in responsive design and modern web technologies.</li>
                                <li>Leveraging Bootstrap to enhance and modernize website design.</li>
                                <li>Focused on achieving a clean and professional appearance while ensuring all
                                    necessary
                                    functionalities are included.
                                </li>
                            </ul>
                            <Image src={nginxImage} alt="NGINX"/>
                        </div>

                        <div className="project">
                            <h2>Backend Development</h2>
                            <ul>
                                <li>Developed backend systems using Java, Python and C#.</li>
                                <li>C# for simple game mechanics from Unity Development.</li>
                                <li>Python for functional and complete applications.</li>
                                <li>Java with databases and simpler programs.</li>
                                <li>Focused on creating efficient and scalable server-side applications.</li>
                            </ul>
                            <Image src={tomcatImage} alt="Backend Development"/>
                        </div>

                        <div className="project">
                            <h2>Server Management</h2>
                            <ul>
                                <li>Set up local and public servers using Node.js and NGINX.</li>
                                <li>Setting up domain certificates, such as vicx.no, using Certbot.</li>
                                <li>Setting up Kubernetes with MicroK8s for easy container management and streamlined
                                    operations.
                                </li>
                            </ul>
                            <Image src={nodeImage} alt="Node.js"/>
                        </div>

                        <div className="project">
                            <h2>Raspberry Pi Server</h2>
                            <ul>
                                <li>Operating a Raspberry Pi server with Raspberry Pi Lite x64 OS.</li>
                                <li>Customizing Kali Linux with multiple desktop environments, including KDE Plasma and
                                    GNOME.
                                </li>
                                <li>Setting up a fully functional Ubuntu server with SSH access configured.</li>
                                <li>Currently running a cluster with two Raspberry Pi devices, using Ubuntu Server, with
                                    one
                                    acting as
                                    the master node.
                                </li>
                            </ul>
                            <Image src={raspberryImage} alt="Raspberry Pi"/>
                        </div>

                        <div className="project">
                            <h2>Linux</h2>
                            <ul>
                                <li>Customizing and optimizing my PC system with Arch Linux with and without the Black
                                    Arch
                                    tools.
                                </li>
                                <li>Exploring different desktop environments and tools for performance tuning.</li>
                                <li>Also familiar with distributions such as Black Arch, Fedora, Ubuntu, Ubuntu Server,
                                    Kali,
                                    ParrotOS &
                                    Debian GNU/Linux.
                                </li>
                            </ul>
                            <Image src={tuxImage} alt="Tux Penguin"/>
                        </div>

                        <div className="project">
                            <h2>Packets</h2>
                            <ul>
                                <li>Packetmanagers used: Pacman, Yay, Flatpak, Snap, Brew, DNF and Apt.</li>
                                <li>Text editors used: Vim, Vi and Nano.</li>
                                <li>Desktops used: KDE Plasma 6, GNOME, Hyprland.</li>
                                <li>Consoles used: Konsole, Kitty, GNOME Terminal and MATE Terminal.</li>
                            </ul>
                            <Image src={archImage} alt="Arch"/>
                        </div>

                        <div className="project">
                            <h2>Database Management</h2>
                            <ul>
                                <li>Proficient in database management with MySQL, H2 and SQLite3.</li>
                                <li>Designing efficient schemas and optimizing query performance.</li>
                            </ul>
                            <Image src={mysqlImage} alt="MySQL"/>
                        </div>
                    </div>
                </div>
            </div>
        </main>
    )
}