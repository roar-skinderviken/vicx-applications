import Hero from "@/components/Hero"
import Image from "next/image"

import tuxImage from "../../assets/images/Tux.png"
import johnImage from "../../assets/images/john.png"
import nmapImage from "../../assets/images/nmap.png"

export const metadata = {
    title: "Penetration Testing | VICX"
}

export default function PenetrationTestingPage() {
    return (
        <main className="content">
            <Hero
                title="Penetration Testing"
                lead="How to use different tools for testing"
            />

            <div className="container mx-auto my-10 px-4">
                <div className="flex justify-center">
                    <div className="lg:w-2/3 md:w-5/6">
                        {/* Section 1: Distros */}
                        <div className="mb-10">
                            <Image src={tuxImage}
                                   alt="Prepare Installation Media"
                                   className="mx-auto mb-5"/>
                            <h3 className="text-3xl font-semibold mb-4 text-left">Distros</h3>
                            <p className="text-sm mb-6">
                                These distros come with the necessary tools to get started and are easy to keep up to
                                date. They are also quite popular, with Black Arch being an exception. ParrotOS is
                                particularly notable
                                as it is the distro used on Hack The Box.
                            </p>

                            {/* Kali Linux */}
                            <div className="mb-6">
                                <strong className="text-xl text-left">Kali Linux</strong>
                                <p className="mt-2 text-left">
                                    Kali Linux is a popular Linux distribution designed for penetration testing and
                                    security research. Built on Debian, it comes with a wide range of pre-installed
                                    tools for tasks like
                                    ethical hacking, vulnerability assessment, and digital forensics. Known for its
                                    robust toolset and
                                    user-friendly interface, Kali Linux is a go-to choice for cybersecurity
                                    professionals and
                                    enthusiasts.
                                </p>
                                <p className="mt-2"><code>Tools: 600+</code></p>
                                <a href="https://www.kali.org/" target="_blank"
                                   className="text-blue-500 underline">https://www.kali.org/</a>
                            </div>

                            {/* ParrotOS */}
                            <div className="mb-6">
                                <strong className="text-xl text-left">ParrotOS</strong>
                                <p className="mt-2 text-left">
                                    ParrotOS is a powerful Linux distro designed for security and privacy. It’s built on
                                    Debian and comes packed with tools for hacking, forensic analysis, and secure
                                    browsing. It’s
                                    user-friendly and great for anyone needing a reliable platform for security testing
                                    and privacy
                                    protection.
                                </p>
                                <p className="mt-2"><code>Tools: 600+</code></p>
                                <a href="https://parrotsec.org/" target="_blank"
                                   className="text-blue-500 underline">https://parrotsec.org/</a>
                            </div>

                            {/* Black Arch Linux */}
                            <div>
                                <strong className="text-xl text-left">Black Arch Linux</strong>
                                <p className="mt-2 text-left">
                                    Black Arch, based on Arch Linux, can be installed on top of a vanilla Arch system,
                                    offering flexibility for existing Arch users. Black Arch offers a vast collection of
                                    tools
                                    for penetration testing, vulnerability assessment, and exploitation. Its rolling
                                    release model
                                    ensures you always have access to the latest tools and updates.
                                </p>
                                <p className="mt-2"><code>Tools: 2900+</code></p>
                                <a href="https://blackarch.org/" target="_blank"
                                   className="text-blue-500 underline">https://blackarch.org/</a>
                            </div>
                        </div>

                        {/* Section 2: John */}
                        <div className="mb-10">
                            <Image src={johnImage}
                                   alt="Prepare Installation Media"
                                   className="mx-auto mb-5"/>
                            <h3 className="text-3xl font-semibold mb-4 text-left">John</h3>
                            <p className="text-left">
                                John is a brute force tool used to crack user passwords by attempting various
                                combinations until it succeeds. It comes with a built-in default wordlist, but you can
                                also use custom
                                wordlists, as demonstrated below. The standard wordlist for penetration testing distros,
                                such as Kali
                                Linux, is the rockyou.txt wordlist, which is included by default.
                            </p>
                            <ol className="list-decimal list-inside text-left mt-4 space-y-2">
                                <li>
                                    Create a <code>example.txt</code> file using: <code>{`touch example.txt`}</code>.
                                </li>
                                <li>
                                    Encrypt the file with a
                                    password: <code>{`zip -P 1234 encrypted_example.zip example.txt`}</code>.
                                </li>
                                <li>
                                    Use John to get the hashes: <code>{`zip2john /path/to/your.zip > hash.txt`}</code>.
                                </li>
                                <li>
                                    Execute a brute force attack: <code>{`John hash.txt`}</code>.
                                </li>
                                <li>
                                    Use a custom
                                    wordlist: <code>{`john hash.txt --wordlist=/path/to/your/wordlist.txt`}</code>.
                                </li>
                            </ol>
                            <p className="text-left mt-4">
                                Keep in mind that the location of <code>rockyou.txt</code> is in <code>/usr</code> on
                                Kali, making this a <code>sudo</code> command.
                            </p>
                        </div>

                        {/* Section 3: Nmap */}
                        <div className="mb-10">
                            <Image src={nmapImage}
                                   alt="Prepare Installation Media"
                                   className="mx-auto mb-5"/>
                            <h3 className="text-3xl font-semibold mb-4 text-left">Nmap</h3>
                            <p className="text-left">
                                Nmap is a network scanning tool used to identify open ports on a network. By detecting
                                these ports, Nmap helps in assessing the security of systems and understanding the
                                services running
                                on them.
                            </p>
                            <ol className="list-decimal list-inside text-left mt-4 space-y-2">
                                <li>To scan a network: <code>nmap 192.168.1.1</code></li>
                                <li>Detect services running on open ports: <code>nmap -sV 192.168.1.1</code></li>
                                <li>Gather OS version info: <code>nmap -O 192.168.1.1</code></li>
                                <li>Perform an aggressive scan: <code>nmap -A 192.168.1.1</code></li>
                                <li>Specify ports to scan: <code>nmap -p 22,80,443 192.168.1.1</code></li>
                            </ol>
                        </div>
                    </div>
                </div>
            </div>
        </main>
    )
}
